package com.seohamin.campon.domain.campsite.service;

import com.seohamin.campon.domain.campsite.dto.CampsiteRecommendResponseDto;
import com.seohamin.campon.domain.campsite.mapper.CampsiteProfileMapper;
import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.CampsiteScoreCalculator;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScoreResult;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import com.seohamin.campon.global.dto.PageNationDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.tourApi.TourApiClient;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import com.seohamin.campon.global.util.EnumUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampsiteRecommendService {

    // TourAPI 전체 조회 시 페이지 크기와 안전 상한 (최대 1,000건)
    private static final int FETCH_PAGE_SIZE = 100;
    private static final int MAX_FETCH_PAGES = 10;

    private final TourApiClient tourApiClient;
    private final CampsiteProfileMapper campsiteProfileMapper;
    private final CampsiteScoreCalculator campsiteScoreCalculator;

    // 점수가 매겨진 캠핑장
    private record ScoredCampsite(CampsiteProfile profile, ScoreResult scoreResult) { }

    /**
     * 유저 조건 기반으로 캠핑장을 점수순 추천하는 메서드
     * @param lat 조회 위도
     * @param lon 조회 경도
     * @param radius 조회 반경 (m단위, 최대 20,000m)
     * @param date 이용 예정 날짜
     * @param groupSize 인원 수
     * @param withCar 차량 동반 여부
     * @param preferredConditions 선호 시설 (Facility enum 이름 목록)
     * @param equipments 보유 장비 (Equipment enum 이름 목록)
     * @param size 페이지 크기
     * @param page 페이지 번호 (0번부터 시작)
     * @return 점수 내림차순 정렬된 페이지네이션 DTO
     */
    public PageNationDto<CampsiteRecommendResponseDto> getCampsiteRecommend(
            final Double lat,
            final Double lon,
            final Integer radius,
            final LocalDateTime date,
            final Integer groupSize,
            final Boolean withCar,
            final List<String> preferredConditions,
            final List<String> equipments,
            final Integer size,
            final Integer page
    ) {
        // 1) 파라미터 검사
        if (
                lat == null || lon == null || radius == null || date == null || groupSize == null
                || withCar == null || preferredConditions == null || equipments == null
                || size == null || page == null
                || radius > 20000 || radius < 0 || groupSize < 1
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }
        if (size < 1 || page < 0 || size > 100) {
            throw new CustomException(ExceptionCode.INVALID_PAGING_PARAMETER);
        }

        // 2) 문자열 파라미터를 enum으로 변환
        final Set<Facility> preferred = toEnumSet(Facility.class, preferredConditions);
        final Set<Equipment> owned = toEnumSet(Equipment.class, equipments);

        final ScoreContext context = new ScoreContext(
                lat, lon, radius, date.toLocalDate(), groupSize, withCar, preferred, owned
        );

        // 3) 반경 내 전체 캠핑장 조회 (점수 정렬은 전역 순위가 필요해 페이지 단위 조회 불가)
        final List<NearbyApiResponseDto.Item> items = fetchAllWithinRadius(lat, lon, radius);

        // 4) 정규화 → 하드 필터 → 점수 계산 → 정렬
        final List<ScoredCampsite> ranked = items.stream()
                .map(item -> campsiteProfileMapper.toProfile(item, lat, lon))
                .filter(profile -> profile.lat() != null && profile.lon() != null) // 좌표 없으면 제외
                .filter(profile -> profile.isOperableOn(context.date()))           // 휴장/운영중지 제외
                .map(profile -> new ScoredCampsite(profile, campsiteScoreCalculator.calculate(profile, context)))
                .sorted(
                        Comparator.comparingInt((ScoredCampsite scored) -> scored.scoreResult().total()).reversed()
                                .thenComparing(scored -> scored.profile().distance()) // 동점이면 가까운 순
                )
                .toList();

        // 5) 메모리 페이지네이션
        return paginate(ranked, size, page);
    }

    // 문자열 리스트를 enum Set으로 변환하는 메서드 (잘못된 값이면 예외)
    private <T extends Enum<T>> Set<T> toEnumSet(final Class<T> enumClass, final List<String> values) {
        return values.stream()
                .map(value -> EnumUtil.toEnum(enumClass, value)
                        .orElseThrow(() -> new CustomException(ExceptionCode.INVALID_ENUM_VALUE)))
                .collect(Collectors.toSet());
    }

    // 반경 내 전체 캠핑장을 페이지 순회하며 조회하는 메서드
    private List<NearbyApiResponseDto.Item> fetchAllWithinRadius(
            final double lat,
            final double lon,
            final int radius
    ) {
        final List<NearbyApiResponseDto.Item> all = new ArrayList<>();

        for (int pageNo = 1; pageNo <= MAX_FETCH_PAGES; pageNo++) {
            final NearbyApiResponseDto apiResult =
                    tourApiClient.getCampsiteNearby(lat, lon, radius, FETCH_PAGE_SIZE, pageNo);
            final NearbyApiResponseDto.Body body = apiResult.response().body();
            final List<NearbyApiResponseDto.Item> items = body.items() == null || body.items().item() == null
                    ? List.of()
                    : body.items().item();

            all.addAll(items);

            // 마지막 페이지면 종료
            if (items.isEmpty() || (long) body.pageNo() * body.numOfRows() >= body.totalCount()) {
                break;
            }
        }

        return all;
    }

    // 정렬된 전체 결과를 메모리에서 슬라이싱하는 메서드
    private PageNationDto<CampsiteRecommendResponseDto> paginate(
            final List<ScoredCampsite> ranked,
            final int size,
            final int page
    ) {
        final int from = page * size;
        final List<CampsiteRecommendResponseDto> content = from >= ranked.size()
                ? List.of()
                : ranked.subList(from, Math.min(from + size, ranked.size())).stream()
                        .map(this::toResponseDto)
                        .toList();

        final boolean hasNext = (long) (page + 1) * size < ranked.size();

        return new PageNationDto<>(hasNext, content);
    }

    // 응답 DTO로 변환하는 메서드
    private CampsiteRecommendResponseDto toResponseDto(final ScoredCampsite scored) {
        final CampsiteProfile profile = scored.profile();
        return new CampsiteRecommendResponseDto(
                profile.contentId(),
                scored.scoreResult().total(),
                scored.scoreResult().policyScores().stream()
                        .map(policyScore -> new CampsiteRecommendResponseDto.ScoreDetail(
                                policyScore.name(), policyScore.score(), policyScore.weight()
                        ))
                        .toList(),
                profile.name(),
                profile.lineIntro(),
                profile.intro(),
                profile.lat(),
                profile.lon(),
                profile.distance(),
                profile.zipcode(),
                profile.tel(),
                profile.resveUrl(),
                List.copyOf(profile.facilities()),
                profile.thumbnailUrl(),
                profile.trailerAllowed(),
                profile.caravanAllowed(),
                profile.toiletCount(),
                profile.showerCount(),
                profile.sinkCount(),
                List.copyOf(profile.rentalEquipments())
        );
    }
}