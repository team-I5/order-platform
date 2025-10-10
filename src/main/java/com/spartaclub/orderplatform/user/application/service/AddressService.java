package com.spartaclub.orderplatform.user.application.service;

import com.spartaclub.orderplatform.user.application.mapper.AddressMapper;
import com.spartaclub.orderplatform.user.domain.entity.Address;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.infrastructure.repository.AddressRepository;
import com.spartaclub.orderplatform.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.user.presentation.dto.AddressListPageResponseDto;
import com.spartaclub.orderplatform.user.presentation.dto.AddressListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 주소 비즈니스 로직 서비스
 * 주소 등록, 조회, 수정, 삭제 등의 비즈니스 로직 처리
 *
 * @author 전우선
 * @date 2025-10-10(금)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    /**
     * 주소 등록
     * 새로운 주소를 등록하고 기본 주소 설정 관리
     *
     * @param requestDto 주소 등록 요청 데이터
     * @param user       주소를 등록하는 사용자
     * @return 등록된 주소 정보
     * @throws RuntimeException 중복 주소명 또는 기본 주소 설정 오류 시
     */
    @Transactional
    public AddressCreateResponseDto createAddress(AddressCreateRequestDto requestDto, User user) {

        // 1. 주소명 중복 체크
        validateDuplicateAddressName(requestDto.getAddressName(), user);

        // 2. 기본 주소 설정 처리
        handleDefaultAddress(requestDto, user);

        // 3. Address 엔티티 생성 (MapStruct 사용)
        Address address = addressMapper.toEntityFromCreateRequest(requestDto);
        address.setUser(user);

        // 4. 첫 번째 주소인 경우 자동으로 기본 주소로 설정
        if (isFirstAddress(user)) {
            address.setDefaultAddress(true);
        }

        // 5. 데이터베이스 저장
        Address savedAddress = addressRepository.save(address);

        // 6. 응답 DTO 생성 (Builder 패턴)
        return addressMapper.toCreateResponse(savedAddress);
    }

    /**
     * 주소명 중복 체크
     * 동일한 사용자의 활성 주소 중 같은 주소명이 있는지 확인
     *
     * @param addressName 확인할 주소명
     * @param user        사용자
     * @throws RuntimeException 중복 주소명 발견 시
     */
    private void validateDuplicateAddressName(String addressName, User user) {
        if (addressRepository.existsByUserAndAddressNameAndDeletedAtIsNull(user, addressName)) {
            throw new RuntimeException("동일한 주소명이 이미 존재합니다.");
        }
    }

    /**
     * 기본 주소 설정 처리
     * 새로운 기본 주소 설정 시 기존 기본 주소를 해제
     *
     * @param requestDto 주소 등록 요청 데이터
     * @param user       사용자
     * @throws RuntimeException 이미 기본 주소가 설정되어 있을 때
     */
    private void handleDefaultAddress(AddressCreateRequestDto requestDto, User user) {
        if (Boolean.TRUE.equals(requestDto.getDefaultAddress())) {
            Optional<Address> existingDefaultAddress = addressRepository
                    .findByUserAndDefaultAddressTrueAndDeletedAtIsNull(user);

            if (existingDefaultAddress.isPresent()) {
                // 기존 기본 주소를 일반 주소로 변경
                Address currentDefault = existingDefaultAddress.get();
                currentDefault.setDefaultAddress(false);
                addressRepository.save(currentDefault);
            }
        }
    }

    /**
     * 사용자의 첫 번째 주소인지 확인
     * 활성 주소가 없는 경우 첫 번째 주소로 판단
     *
     * @param user 사용자
     * @return 첫 번째 주소 여부
     */
    private boolean isFirstAddress(User user) {
        return addressRepository.countByUserAndDeletedAtIsNull(user) == 0;
    }

    /**
     * 주소 목록 조회
     * 사용자의 주소 목록을 조회하고 통계 정보와 함께 반환
     *
     * @param includeDeleted 삭제된 주소 포함 여부
     * @param user           주소를 조회하는 사용자
     * @return 주소 목록과 통계 정보
     */
    @Transactional(readOnly = true)
    public AddressListPageResponseDto getAllAddresses(Boolean includeDeleted, User user) {

        // 1. 주소 목록 조회 (기본 주소 우선, 생성일시 최신순)
        List<Address> addresses;
        if (Boolean.TRUE.equals(includeDeleted)) {
            addresses = addressRepository.findByUserOrderByDefaultAddressDescCreatedAtDesc(user);
        } else {
            addresses = addressRepository.findByUserAndDeletedAtIsNullOrderByDefaultAddressDescCreatedAtDesc(user);
        }

        // 2. Address -> AddressListResponseDto 변환
        List<AddressListResponseDto> addressList = addresses.stream()
                .map(addressMapper::toListResponse)
                .collect(java.util.stream.Collectors.toList());

        // 3. 기본 주소 조회
        Address defaultAddress = addressRepository
                .findByUserAndDefaultAddressTrueAndDeletedAtIsNull(user)
                .orElse(null);

        // 4. 총 주소 개수 계산
        long totalCount = Boolean.TRUE.equals(includeDeleted)
                ? addresses.size()
                : addressRepository.countByUserAndDeletedAtIsNull(user);

        // 5. Mapper를 통한 응답 DTO 생성
        return addressMapper.toPageResponse(addressList, totalCount, defaultAddress);
    }
}