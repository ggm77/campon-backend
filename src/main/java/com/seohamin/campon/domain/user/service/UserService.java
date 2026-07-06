package com.seohamin.campon.domain.user.service;

import com.seohamin.campon.domain.user.dto.UserRequestDto;
import com.seohamin.campon.domain.user.dto.UserResponseDto;
import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.domain.user.repository.UserRepository;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.util.EnumUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 upsert 하는 메서드
     * @param userRequestDto 유저 정보 담긴 DTO
     * @return upsert된 유저 정보
     */
    @Transactional
    public UserResponseDto upsertUser(final UserRequestDto userRequestDto) {
        // 1) null 검사
        if (userRequestDto == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }
        if (
                userRequestDto.hasCar() == null || userRequestDto.preferredConditions() == null
                || userRequestDto.equipment() == null
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) List<String>을 Set<Enum>으로 변환
        final Set<Facility> facilitySet = new HashSet<>(toEnumList(userRequestDto.preferredConditions(), Facility.class));
        final Set<Equipment> equipmentSet = new HashSet<>(toEnumList(userRequestDto.equipment(), Equipment.class));


        // 3) 유저 엔티티 생성
        final User user = User.builder()
                .hasCar(userRequestDto.hasCar())
                .preferredConditions(facilitySet)
                .equipments(equipmentSet)
                .build();

        // 4) 유저 저장
        final User savedUser = userRepository.save(user);

        return UserResponseDto.of(savedUser);
    }

    /**
     * String 리스트로 들어온걸 Enum 리스트로 변환하는 메서드
     * @param list 변환할 리스트
     * @param clazz 변환할 Enum 클래스
     * @return Enum 리스트
     * @param <T> Enum 타입
     */
    private <T extends Enum<T>> List<T> toEnumList(final List<String> list, final Class<T> clazz) {
        // 1) 빈 리스트 생성
        final List<T> enumList = new ArrayList<>();

        // 2) null 검사
        if (list == null || list.isEmpty()) {
            return enumList;
        }

        // 3) 변환
        for (String value : list) {
            Optional<T> e = EnumUtil.toEnum(clazz, value);
            e.ifPresent(enumList::add);
        }

        return enumList;
    }
}
