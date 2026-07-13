package com.seohamin.campon.domain.campsite.service;

import com.seohamin.campon.domain.campsite.dto.CampsiteRecommendResponseDto;
import com.seohamin.campon.domain.campsite.mapper.CampsiteProfileMapper;
import com.seohamin.campon.domain.campsite.score.CampsiteScoreCalculator;
import com.seohamin.campon.domain.campsite.score.policy.DistanceScorePolicy;
import com.seohamin.campon.domain.campsite.score.policy.EquipmentRentalScorePolicy;
import com.seohamin.campon.domain.campsite.score.policy.FacilityScorePolicy;
import com.seohamin.campon.domain.campsite.score.policy.SafetyScorePolicy;
import com.seohamin.campon.domain.campsite.score.policy.SiteTypeScorePolicy;
import com.seohamin.campon.global.dto.PageNationDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.tourApi.TourApiClient;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.seohamin.campon.domain.campsite.fixture.CampsiteItemFixture.item;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampsiteRecommendServiceTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 8, 1, 0, 0);

    @Mock
    private TourApiClient tourApiClient;

    private CampsiteRecommendService service;

    @BeforeEach
    void setUp() {
        service = new CampsiteRecommendService(
                tourApiClient,
                new CampsiteProfileMapper(),
                new CampsiteScoreCalculator(List.of(
                        new FacilityScorePolicy(),
                        new EquipmentRentalScorePolicy(),
                        new DistanceScorePolicy(),
                        new SiteTypeScorePolicy(),
                        new SafetyScorePolicy()
                ))
        );
    }

    private static NearbyApiResponseDto response(
            final List<NearbyApiResponseDto.Item> items,
            final int pageNo,
            final int totalCount
    ) {
        return new NearbyApiResponseDto(new NearbyApiResponseDto.Response(
                new NearbyApiResponseDto.Header("0000", "OK"),
                new NearbyApiResponseDto.Body(
                        new NearbyApiResponseDto.Items(items), 100, pageNo, totalCount
                )
        ));
    }

    private void givenApiReturns(final List<NearbyApiResponseDto.Item> items) {
        when(tourApiClient.getCampsiteNearby(anyDouble(), anyDouble(), anyInt(), anyInt(), anyInt()))
                .thenReturn(response(items, 1, items.size()));
    }

    @Test
    @DisplayName("선호 조건을 더 충족하는 캠핑장이 더 높은 점수로 앞에 정렬된다")
    void sortsByScoreDesc() {
        givenApiReturns(List.of(
                item(Map.of("contentId", "2")), // 시설 없음
                item(Map.of("contentId", "1", "sbrsCl", "전기,온수", "insrncAt", "Y")) // 선호 충족
        ));

        final PageNationDto<CampsiteRecommendResponseDto> result = service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false,
                List.of("ELECTRICITY", "HOT_WATER"), List.of(), 10, 0
        );

        assertThat(result.items()).hasSize(2);
        assertThat(result.items().get(0).campsiteId()).isEqualTo(1L);
        assertThat(result.items().get(0).score()).isGreaterThan(result.items().get(1).score());
        assertThat(result.items()).allSatisfy(dto ->
                assertThat(dto.score()).isBetween(0, 100));
    }

    @Test
    @DisplayName("항목별 점수가 5개 정책 모두에 대해 0~100 범위로 내려간다")
    void includesScoreDetails() {
        givenApiReturns(List.of(
                item(Map.of("contentId", "1", "sbrsCl", "전기,온수", "insrncAt", "Y"))
        ));

        final PageNationDto<CampsiteRecommendResponseDto> result = service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false,
                List.of("ELECTRICITY"), List.of(), 10, 0
        );

        final CampsiteRecommendResponseDto dto = result.items().get(0);
        assertThat(dto.scoreDetails())
                .extracting(CampsiteRecommendResponseDto.ScoreDetail::name)
                .containsExactlyInAnyOrder("facility", "equipmentRental", "distance", "siteType", "safety");
        assertThat(dto.scoreDetails()).allSatisfy(detail -> {
            assertThat(detail.score()).isBetween(0, 100);
            assertThat(detail.weight()).isPositive();
        });
        // 선호 시설(전기)을 충족했으므로 facility 항목은 만점
        assertThat(dto.scoreDetails())
                .filteredOn(detail -> detail.name().equals("facility"))
                .first()
                .extracting(CampsiteRecommendResponseDto.ScoreDetail::score)
                .isEqualTo(100);
    }

    @Test
    @DisplayName("휴장 중이거나 휴장 기간에 걸리거나 좌표가 없는 캠핑장은 제외된다")
    void hardFilters() {
        givenApiReturns(List.of(
                item(Map.of("contentId", "1")),
                item(Map.of("contentId", "2", "manageSttus", "휴장")),
                item(Map.of("contentId", "3", "hvofBgnde", "2026-07-15", "hvofEnddle", "2026-08-15")),
                item(Map.of("contentId", "4", "mapX", "", "mapY", ""))
        ));

        final PageNationDto<CampsiteRecommendResponseDto> result = service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false, List.of(), List.of(), 10, 0
        );

        assertThat(result.items())
                .extracting(CampsiteRecommendResponseDto::campsiteId)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("점수 정렬 후 메모리에서 페이지네이션하고 hasNext를 계산한다")
    void paginatesAfterSorting() {
        givenApiReturns(List.of(
                item(Map.of("contentId", "1")),
                item(Map.of("contentId", "2")),
                item(Map.of("contentId", "3"))
        ));

        final PageNationDto<CampsiteRecommendResponseDto> firstPage = service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false, List.of(), List.of(), 2, 0
        );
        final PageNationDto<CampsiteRecommendResponseDto> lastPage = service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false, List.of(), List.of(), 2, 1
        );

        assertThat(firstPage.items()).hasSize(2);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(lastPage.items()).hasSize(1);
        assertThat(lastPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("잘못된 enum 이름이 들어오면 INVALID_ENUM_VALUE 예외가 발생한다")
    void invalidEnumValue() {
        assertThatThrownBy(() -> service.getCampsiteRecommend(
                37.5, 127.0, 10000, DATE, 2, false,
                List.of("NOT_A_FACILITY"), List.of(), 10, 0
        ))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_ENUM_VALUE);
    }

    @Test
    @DisplayName("반경이 20km를 넘으면 INVALID_REQUEST 예외가 발생한다")
    void invalidRadius() {
        assertThatThrownBy(() -> service.getCampsiteRecommend(
                37.5, 127.0, 20001, DATE, 2, false, List.of(), List.of(), 10, 0
        ))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }
}