package com.spartaclub.orderplatform.domain.user.application.service;

import com.spartaclub.orderplatform.domain.user.application.mapper.AddressMapper;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import com.spartaclub.orderplatform.domain.user.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 주소 비즈니스 로직 서비스
 * 주소 등록, 조회, 수정, 삭제 등의 비즈니스 로직 처리
 *
 * @author 전우선
 * @date 2025-10-12(일)
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
        if (addressRepository.existsByUserAndAddressName(user, addressName)) {
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
                    .findDefaultByUser(user);

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
        return addressRepository.countByUser(user) == 0;
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
            addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        } else {
            addresses = addressRepository.findActiveByUserOrderByIsDefaultDescCreatedAtDesc(user);
        }

        // 2. Address -> AddressListResponseDto 변환
        List<AddressListResponseDto> addressList = addresses.stream()
                .map(addressMapper::toListResponse)
                .collect(java.util.stream.Collectors.toList());

        // 3. 기본 주소 조회
        Address defaultAddress = addressRepository
                .findDefaultByUser(user)
                .orElse(null);

        // 4. 총 주소 개수 계산
        long totalCount = Boolean.TRUE.equals(includeDeleted)
                ? addresses.size()
                : addressRepository.countByUser(user);

        // 5. Mapper를 통한 응답 DTO 생성
        return addressMapper.toPageResponse(addressList, totalCount, defaultAddress);
    }

    /**
     * 주소 수정
     * 기존 주소 정보를 수정하고 소유자 검증 및 기본 주소 관리 수행
     *
     * @param addressId  수정할 주소 ID
     * @param requestDto 주소 수정 요청 데이터
     * @param user       주소를 수정하는 사용자
     * @return 수정된 주소 정보
     * @throws RuntimeException 주소를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public AddressUpdateResponseDto updateAddress(UUID addressId, AddressUpdateRequestDto requestDto, User user) {

        // 1. 주소 조회 및 소유자 검증
        Address address = findAddressAndValidateOwner(addressId, user);

        // 2. 삭제된 주소 수정 불가 검증
        validateNotDeleted(address);

        // 3. 주소명 중복 체크 (본인 제외)
        validateDuplicateAddressNameForUpdate(requestDto.getAddressName(), user, addressId);

        // 4. 기본 주소 변경 처리
        handleDefaultAddressForUpdate(requestDto, user, address);

        // 5. 주소 정보 업데이트
        updateAddressFields(address, requestDto);

        // 6. 데이터베이스 저장
        Address savedAddress = addressRepository.save(address);

        // 7. 응답 DTO 생성
        return addressMapper.toUpdateResponse(savedAddress);
    }

    /**
     * 주소 조회 및 소유자 검증
     *
     * @param addressId 주소 ID
     * @param user      사용자
     * @return 조회된 주소
     * @throws RuntimeException 주소를 찾을 수 없거나 소유자가 아닌 경우
     */
    private Address findAddressAndValidateOwner(UUID addressId, User user) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다."));

        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 주소에 접근할 권한이 없습니다.");
        }

        return address;
    }

    /**
     * 삭제된 주소 수정 불가 검증
     *
     * @param address 주소
     * @throws RuntimeException 삭제된 주소인 경우
     */
    private void validateNotDeleted(Address address) {
        if (address.getDeletedAt() != null) {
            throw new RuntimeException("삭제된 주소는 수정할 수 없습니다.");
        }
    }

    /**
     * 주소명 중복 체크 (수정용)
     * 본인 주소 제외하고 동일한 주소명이 있는지 확인
     *
     * @param addressName 확인할 주소명
     * @param user        사용자
     * @param excludeId   제외할 주소 ID (본인)
     * @throws RuntimeException 중복 주소명 발견 시
     */
    private void validateDuplicateAddressNameForUpdate(String addressName, User user, UUID excludeId) {
        Optional<Address> existingAddress = addressRepository
                .findByUser(user).stream()
                .filter(addr -> addr.getAddressName().equals(addressName))
                .findFirst();

        if (existingAddress.isPresent() && !existingAddress.get().getAddressId().equals(excludeId)) {
            throw new RuntimeException("동일한 주소명이 이미 존재합니다.");
        }
    }

    /**
     * 기본 주소 변경 처리 (수정용)
     *
     * @param requestDto 수정 요청 데이터
     * @param user       사용자
     * @param address    수정할 주소
     * @throws RuntimeException 기본 주소 해제 불가한 경우
     */
    private void handleDefaultAddressForUpdate(AddressUpdateRequestDto requestDto, User user, Address address) {
        boolean newDefaultValue = Boolean.TRUE.equals(requestDto.getDefaultAddress());
        boolean currentDefaultValue = Boolean.TRUE.equals(address.getDefaultAddress());

        // 기본 주소로 설정하는 경우
        if (newDefaultValue && !currentDefaultValue) {
            // 기존 기본 주소 해제
            Optional<Address> existingDefaultAddress = addressRepository
                    .findDefaultByUser(user);

            if (existingDefaultAddress.isPresent()) {
                Address currentDefault = existingDefaultAddress.get();
                currentDefault.setDefaultAddress(false);
                addressRepository.save(currentDefault);
            }
        }
        // 기본 주소를 해제하는 경우
        else if (!newDefaultValue && currentDefaultValue) {
            // 다른 기본 주소가 있는지 확인
            long activeAddressCount = addressRepository.countByUser(user);
            if (activeAddressCount <= 1) {
                throw new RuntimeException("마지막 주소는 기본 주소를 해제할 수 없습니다.");
            }

            // 다른 주소를 기본 주소로 설정
            List<Address> otherAddresses = addressRepository
                    .findActiveByUserExcludingId(user, address.getAddressId());

            if (!otherAddresses.isEmpty()) {
                Address newDefaultAddress = otherAddresses.get(0);
                newDefaultAddress.setDefaultAddress(true);
                addressRepository.save(newDefaultAddress);
            }
        }
    }

    /**
     * 주소 필드 업데이트
     *
     * @param address    업데이트할 주소
     * @param requestDto 수정 요청 데이터
     */
    private void updateAddressFields(Address address, AddressUpdateRequestDto requestDto) {
        address.setAddressName(requestDto.getAddressName());
        address.setName(requestDto.getName());
        address.setPhoneNumber(requestDto.getPhoneNumber());
        address.setPostCode(requestDto.getPostCode());
        address.setRoadNameAddress(requestDto.getRoadNameAddress());
        address.setDetailedAddress(requestDto.getDetailedAddress());
        address.setDefaultAddress(requestDto.getDefaultAddress());
    }

    /**
     * 주소 삭제 (Soft Delete)
     * 기존 주소를 소프트 삭제하고 기본 주소 보호 및 최소 주소 보장 로직 수행
     *
     * @param addressId 삭제할 주소 ID
     * @param user      주소를 삭제하는 사용자
     * @return 삭제된 주소 정보
     * @throws RuntimeException 삭제 불가능한 경우
     */
    @Transactional
    public AddressDeleteResponseDto deleteAddress(UUID addressId, User user) {

        // 1. 주소 조회 및 소유자 검증
        Address address = findAddressAndValidateOwner(addressId, user);

        // 2. 이미 삭제된 주소 체크
        validateNotAlreadyDeleted(address);

        // 3. 마지막 주소 삭제 방지
        validateNotLastAddress(user);

        // 4. 기본 주소인 경우 다른 주소를 기본으로 설정
        handleDefaultAddressForDelete(address, user);

        // 5. Soft Delete 실행
        performSoftDelete(address);

        // 6. 응답 DTO 생성
        return addressMapper.toDeleteResponse(address);
    }

    /**
     * 이미 삭제된 주소 체크
     *
     * @param address 주소
     * @throws RuntimeException 이미 삭제된 주소인 경우
     */
    private void validateNotAlreadyDeleted(Address address) {
        if (address.getDeletedAt() != null) {
            throw new RuntimeException("이미 삭제된 주소입니다.");
        }
    }

    /**
     * 마지막 주소 삭제 방지
     * 사용자는 최소 1개의 활성 주소를 보유해야 함
     *
     * @param user 사용자
     * @throws RuntimeException 마지막 주소인 경우
     */
    private void validateNotLastAddress(User user) {
        long activeAddressCount = addressRepository.countByUser(user);
        if (activeAddressCount <= 1) {
            throw new RuntimeException("마지막 주소는 삭제할 수 없습니다.");
        }
    }

    /**
     * 기본 주소 삭제 시 다른 주소를 자동으로 기본 주소로 설정
     * 기본 주소가 아닌 경우 아무 작업 안 함
     *
     * @param address 삭제할 주소
     * @param user    사용자
     */
    private void handleDefaultAddressForDelete(Address address, User user) {
        // 기본 주소가 아닌 경우 처리 불필요
        if (!Boolean.TRUE.equals(address.getDefaultAddress())) {
            return;
        }

        // 삭제할 주소를 제외한 다른 활성 주소 목록 조회 (최신순)
        List<Address> otherAddresses = addressRepository
                .findActiveByUserExcludingId(user, address.getAddressId());

        // 다른 주소 중 가장 최근에 생성된 주소를 기본 주소로 설정
        if (!otherAddresses.isEmpty()) {
            Address newDefaultAddress = otherAddresses.get(0); // 이미 createdAt 최신순으로 정렬됨
            newDefaultAddress.setDefaultAddress(true);
            addressRepository.save(newDefaultAddress);
        }
        // 다른 주소가 없는 경우는 validateNotLastAddress에서 이미 차단됨
    }

    /**
     * Soft Delete 실행
     * BaseEntity의 delete() 메서드 사용
     *
     * @param address 삭제할 주소
     */
    private void performSoftDelete(Address address) {
        address.delete();
        addressRepository.save(address);
    }
}