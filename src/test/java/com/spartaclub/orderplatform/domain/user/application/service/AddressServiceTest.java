package com.spartaclub.orderplatform.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spartaclub.orderplatform.domain.user.application.mapper.AddressMapper;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import com.spartaclub.orderplatform.domain.user.exception.AddressErrorCode;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressUpdateRequestDto;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AddressService 단위 테스트
 * <p>
 * 주소 서비스의 비즈니스 로직을 검증하는 단위 테스트 - 주소 등록, 조회, 수정, 삭제 등의 핵심 기능 테스트 - 기본 주소 관리 로직 테스트 (첫 번째 주소 자동 설정,
 * 기본 주소 변경 등) - Mock 객체를 사용하여 외부 의존성을 격리하고 순수한 비즈니스 로직만 테스트
 *
 * @author 전우선
 * @since 2025-10-16(목)
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    // === Mock 객체 정의 ===
    // 데이터 접근 계층 Mock
    @Mock
    private AddressRepository addressRepository;

    // 매퍼 Mock
    @Mock
    private AddressMapper addressMapper;

    // 테스트 대상 서비스 (Mock 객체들이 주입됨)
    @InjectMocks
    private AddressService addressService;

    // === 테스트 데이터 ===
    private User testUser;
    private Address testAddress;
    private AddressCreateRequestDto createRequestDto;

    /**
     * 테스트 데이터 초기화
     * 각 테스트 메서드 실행 전에 공통으로 사용할 테스트 데이터를 설정
     */
    @BeforeEach
    void setUp() {
        // 기존 사용자 데이터 (주소 관리 테스트용)
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.CUSTOMER);

        // 기존 주소 데이터 (수정, 삭제 테스트용)
        testAddress = new Address();
        testAddress.setAddressId(UUID.randomUUID());
        testAddress.setAddressName("집");
        testAddress.setName("홍길동");
        testAddress.setPhoneNumber("01012345678");
        testAddress.setPostCode("12345");
        testAddress.setRoadNameAddress("서울시 강남구 테헤란로 427");
        testAddress.setDetailedAddress("101동 1001호");
        testAddress.setDefaultAddress(true);
        testAddress.setUser(testUser);

        // 주소 등록 요청 데이터
        createRequestDto = new AddressCreateRequestDto();
        createRequestDto.setAddressName("회사");
        createRequestDto.setName("김철수");
        createRequestDto.setPhoneNumber("01087654321");
        createRequestDto.setPostCode("54321");
        createRequestDto.setRoadNameAddress("서울시 서초구 강남대로 456");
        createRequestDto.setDetailedAddress("5층");
        createRequestDto.setDefaultAddress(false);
    }

    // === 주소 등록 테스트 ===
    
    /**
     * 주소 등록 성공 테스트
     * 
     * 테스트 시나리오:
     * 1. 중복 검증을 통과한 유효한 주소 등록 요청
     * 2. 주소 엔티티 생성 및 데이터베이스 저장
     * 3. 성공 응답 반환
     * 
     * 검증 사항:
     * - 성공 메시지와 등록된 주소명 반환 확인
     * - 데이터베이스 저장 메서드 호출 확인
     */
    @Test
    @DisplayName("주소 등록 성공")
    void createAddress_success() {
        // Given - 주소 등록 성공 시나리오 Mock 설정
        when(addressRepository.existsByUserAndAddressName(any(User.class), anyString())).thenReturn(
            false); // 주소명 중복 없음
        when(addressRepository.countByUser(any(User.class))).thenReturn(1L);
        when(
            addressMapper.toEntityFromCreateRequest(any(AddressCreateRequestDto.class))).thenReturn(
            testAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        AddressCreateResponseDto mockResponse = AddressCreateResponseDto.builder()
            .addressId(testAddress.getAddressId())
            .addressName("회사")
            .message("주소가 성공적으로 등록되었습니다.")
            .build();
        when(addressMapper.toCreateResponse(any(Address.class))).thenReturn(mockResponse);

        // When - 주소 등록 서비스 호출
        AddressCreateResponseDto result = addressService.createAddress(createRequestDto, testUser);

        // Then - 응답 데이터 및 메서드 호출 검증
        assertThat(result.getAddressName()).isEqualTo("회사");
        assertThat(result.getMessage()).isEqualTo("주소가 성공적으로 등록되었습니다.");
        verify(addressRepository).save(any(Address.class)); // 저장 메서드 호출 확인
    }

    /**
     * 주소 등록 실패 테스트 - 주소명 중복
     * 
     * 테스트 시나리오:
     * 1. 이미 존재하는 주소명으로 주소 등록 시도
     * 2. 주소명 중복 검증에서 실패
     * 3. DUPLICATE_ADDRESS_NAME 에러코드 반환
     * 
     * 검증 사항:
     * - BusinessException 발생 확인
     * - 정확한 에러코드 반환 확인
     */
    @Test
    @DisplayName("주소 등록 실패 - 중복 주소명")
    void createAddress_fail_duplicateAddressName() {
        // Given - 주소명 중복 상황 Mock 설정
        when(addressRepository.existsByUserAndAddressName(any(User.class), anyString())).thenReturn(
            true); // 주소명 중복 상황

        // When & Then - 예외 발생 및 에러코드 검증
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> addressService.createAddress(createRequestDto, testUser)
        );

        assertThat(exception.getErrorCode()).isEqualTo(AddressErrorCode.DUPLICATE_ADDRESS_NAME);
    }

    /**
     * 주소 등록 성공 테스트 - 첫 번째 주소 자동 기본 주소 설정
     * 
     * 테스트 시나리오:
     * 1. 기본 주소로 설정하지 않아도 첫 번째 주소는 자동으로 기본 주소가 됨
     * 2. 비즈니스 루직에 의해 자동으로 defaultAddress = true 로 설정
     * 3. 사용자 편의성을 위한 기능
     * 
     * 검증 사항:
     * - 첫 번째 주소가 자동으로 기본 주소로 설정되는지 확인
     * - 데이터베이스 저장 메서드 호출 확인
     */
    @Test
    @DisplayName("주소 등록 성공 - 첫 번째 주소는 자동으로 기본 주소 설정")
    void createAddress_success_firstAddressAutoDefault() {
        // Given - 첫 번째 주소 등록 시나리오 Mock 설정
        createRequestDto.setDefaultAddress(false); // 기본 주소 설정하지 않음
        when(addressRepository.existsByUserAndAddressName(any(User.class), anyString())).thenReturn(
            false);
        when(addressRepository.countByUser(any(User.class))).thenReturn(0L); // 첫 번째 주소
        when(
            addressMapper.toEntityFromCreateRequest(any(AddressCreateRequestDto.class))).thenReturn(
            testAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When - 주소 등록 서비스 호출
        addressService.createAddress(createRequestDto, testUser);

        // Then - 자동 기본 주소 설정 및 저장 확인
        verify(addressRepository).save(any(Address.class));
        assertThat(testAddress.getDefaultAddress()).isTrue(); // 자동으로 기본 주소로 설정됨
    }

    /**
     * 주소 등록 성공 테스트 - 기본 주소 설정 시 기존 기본 주소 해제
     * 
     * 테스트 시나리오:
     * 1. 새로운 주소를 기본 주소로 설정하여 등록
     * 2. 기존에 있던 기본 주소를 일반 주소로 변경
     * 3. 한 명의 사용자는 하나의 기본 주소만 가질 수 있는 비즈니스 루직
     * 
     * 검증 사항:
     * - 기존 기본 주소가 일반 주소로 변경되는지 확인
     * - 기존 주소와 새로운 주소 모두 저장되는지 확인
     */
    @Test
    @DisplayName("주소 등록 성공 - 기본 주소 설정 시 기존 기본 주소 해제")
    void createAddress_success_replaceDefaultAddress() {
        // Given - 기존 기본 주소가 있는 상황에서 새로운 기본 주소 등록
        Address existingDefaultAddress = new Address();
        existingDefaultAddress.setDefaultAddress(true);
        existingDefaultAddress.setUser(testUser);

        createRequestDto.setDefaultAddress(true); // 새로운 기본 주소로 설정
        when(addressRepository.existsByUserAndAddressName(any(User.class), anyString())).thenReturn(
            false);
        when(addressRepository.countByUser(any(User.class))).thenReturn(1L);
        when(addressRepository.findDefaultByUser(any(User.class))).thenReturn(
            Optional.of(existingDefaultAddress));
        when(
            addressMapper.toEntityFromCreateRequest(any(AddressCreateRequestDto.class))).thenReturn(
            testAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When - 새로운 기본 주소 등록 서비스 호출
        addressService.createAddress(createRequestDto, testUser);

        // Then - 기본 주소 대체 및 저장 확인
        assertThat(existingDefaultAddress.getDefaultAddress()).isFalse(); // 기존 기본 주소 해제
        verify(addressRepository).save(existingDefaultAddress); // 기존 주소 저장
        verify(addressRepository).save(testAddress); // 새로운 주소 저장
    }

    // === 주소 수정 테스트 ===
    
    /**
     * 주소 수정 성공 테스트
     * 
     * 테스트 시나리오:
     * 1. 유효한 수정 데이터로 주소 수정 시도
     * 2. 소유자 검증 및 중복 검증 통과
     * 3. 주소 정보 업데이트 및 데이터베이스 저장
     * 4. 기본 주소 해제 시 다른 주소를 자동으로 기본 주소로 설정
     * 
     * 검증 사항:
     * - 수정된 데이터가 엔티티에 올바르게 반영되었는지 확인
     * - 데이터베이스 저장 메서드 호출 확인
     */
    @Test
    @DisplayName("주소 수정 성공")
    void updateAddress_success() {
        // Given - 주소 수정 요청 데이터 및 Mock 설정
        AddressUpdateRequestDto updateRequestDto = new AddressUpdateRequestDto();
        updateRequestDto.setAddressName("새집");
        updateRequestDto.setName("이영희");
        updateRequestDto.setPhoneNumber("01099998888");
        updateRequestDto.setPostCode("98765");
        updateRequestDto.setRoadNameAddress("부산시 해운대구 센텀로 123");
        updateRequestDto.setDetailedAddress("201호");
        updateRequestDto.setDefaultAddress(false); // 기본 주소 해제

        // 다른 주소 생성 (기본 주소 해제 시 대체할 주소)
        Address anotherAddress = new Address();
        anotherAddress.setAddressId(UUID.randomUUID());
        anotherAddress.setDefaultAddress(false);
        anotherAddress.setUser(testUser);

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));
        when(addressRepository.findByUser(any(User.class))).thenReturn(List.of(testAddress));
        when(addressRepository.countByUser(any(User.class))).thenReturn(2L); // 2개 이상의 주소 보유
        when(addressRepository.findActiveByUserExcludingId(any(User.class), any(UUID.class)))
            .thenReturn(List.of(anotherAddress)); // 기본 주소로 설정할 다른 주소
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When - 주소 수정 서비스 호출
        addressService.updateAddress(testAddress.getAddressId(), updateRequestDto, testUser);

        // Then - 업데이트 결과 및 저장 확인
        verify(addressRepository).save(testAddress);
        assertThat(testAddress.getAddressName()).isEqualTo("새집");
        assertThat(testAddress.getName()).isEqualTo("이영희");
        assertThat(testAddress.getPhoneNumber()).isEqualTo("01099998888");
    }

    @Test
    @DisplayName("주소 수정 실패 - 존재하지 않는 주소")
    void updateAddress_fail_addressNotFound() {
        // Given
        AddressUpdateRequestDto updateRequestDto = new AddressUpdateRequestDto();
        UUID nonExistentId = UUID.randomUUID();

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> addressService.updateAddress(nonExistentId, updateRequestDto, testUser)
        );

        assertThat(exception.getMessage()).contains("주소를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("주소 수정 실패 - 권한 없음")
    void updateAddress_fail_accessDenied() {
        // Given
        User otherUser = new User();
        otherUser.setUserId(2L);

        AddressUpdateRequestDto updateRequestDto = new AddressUpdateRequestDto();
        updateRequestDto.setAddressName("새집");

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> addressService.updateAddress(testAddress.getAddressId(), updateRequestDto,
                otherUser)
        );

        assertThat(exception.getErrorCode()).isEqualTo(AddressErrorCode.ACCESS_DENIED);
    }

    @Test
    @DisplayName("주소 삭제 성공")
    void deleteAddress_success() {
        // Given
        Address anotherAddress = new Address();
        anotherAddress.setAddressId(UUID.randomUUID());
        anotherAddress.setDefaultAddress(false);
        anotherAddress.setUser(testUser);

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));
        when(addressRepository.countByUser(any(User.class))).thenReturn(2L); // 2개 이상의 주소 보유
        when(addressRepository.findActiveByUserExcludingId(any(User.class), any(UUID.class)))
            .thenReturn(List.of(anotherAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        addressService.deleteAddress(testAddress.getAddressId(), testUser);

        // Then
        verify(addressRepository).save(testAddress);
        assertThat(testAddress.getDeletedAt()).isNotNull();
        assertThat(testAddress.getDeletedId()).isEqualTo(testUser.getUserId());
    }

    @Test
    @DisplayName("주소 삭제 실패 - 마지막 주소")
    void deleteAddress_fail_lastAddress() {
        // Given
        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));
        when(addressRepository.countByUser(any(User.class))).thenReturn(1L); // 마지막 주소

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> addressService.deleteAddress(testAddress.getAddressId(), testUser)
        );

        assertThat(exception.getErrorCode()).isEqualTo(AddressErrorCode.CANNOT_DELETE_LAST_ADDRESS);
    }

    @Test
    @DisplayName("주소 삭제 실패 - 이미 삭제된 주소")
    void deleteAddress_fail_alreadyDeleted() {
        // Given
        testAddress.delete(testUser.getUserId()); // 이미 삭제된 상태

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> addressService.deleteAddress(testAddress.getAddressId(), testUser)
        );

        assertThat(exception.getErrorCode()).isEqualTo(AddressErrorCode.ALREADY_DELETED);
    }

    @Test
    @DisplayName("기본 주소 삭제 시 다른 주소가 자동으로 기본 주소로 설정")
    void deleteAddress_success_autoSetNewDefault() {
        // Given
        Address anotherAddress = new Address();
        anotherAddress.setAddressId(UUID.randomUUID());
        anotherAddress.setDefaultAddress(false);
        anotherAddress.setUser(testUser);

        testAddress.setDefaultAddress(true); // 기본 주소

        when(addressRepository.findById(any(UUID.class))).thenReturn(Optional.of(testAddress));
        when(addressRepository.countByUser(any(User.class))).thenReturn(2L);
        when(addressRepository.findActiveByUserExcludingId(any(User.class), any(UUID.class)))
            .thenReturn(List.of(anotherAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // When
        addressService.deleteAddress(testAddress.getAddressId(), testUser);

        // Then
        verify(addressRepository).save(anotherAddress); // 다른 주소가 기본 주소로 설정됨
        assertThat(anotherAddress.getDefaultAddress()).isTrue();
    }

    @Test
    @DisplayName("주소 목록 조회 성공")
    void getAllAddresses_success() {
        // Given
        List<Address> addresses = List.of(testAddress);
        when(addressRepository.findActiveByUserOrderByIsDefaultDescCreatedAtDesc(any(User.class)))
            .thenReturn(addresses);
        when(addressRepository.findDefaultByUser(any(User.class))).thenReturn(
            Optional.of(testAddress));
        when(addressRepository.countByUser(any(User.class))).thenReturn(1L);

        // When
        addressService.getAllAddresses(false, testUser);

        // Then
        verify(addressRepository).findActiveByUserOrderByIsDefaultDescCreatedAtDesc(testUser);
        verify(addressRepository).findDefaultByUser(testUser);
    }
}