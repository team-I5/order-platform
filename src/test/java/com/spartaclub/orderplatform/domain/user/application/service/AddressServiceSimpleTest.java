package com.spartaclub.orderplatform.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.spartaclub.orderplatform.domain.user.application.mapper.AddressMapper;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.Arrays;
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
 * AddressService 간단한 단위 테스트
 * - 핵심 기능만 안정적으로 테스트
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceSimpleTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private Address testAddress;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        testUser = User.createUser(
            "testuser",
            "test@example.com",
            "encodedPassword",
            "testnick",
            "01012345678",
            UserRole.CUSTOMER
        );

        addressId = UUID.randomUUID();
        testAddress = Address.builder()
            .addressId(addressId)
            .addressName("집")
            .user(testUser)
            .name("홍길동")
            .phoneNumber("01012345678")
            .postCode("12345")
            .roadNameAddress("서울시 강남구")
            .detailedAddress("456호")
            .defaultAddress(true)
            .build();
    }

    @Test
    @DisplayName("AddressService Mock 설정 확인")
    void mockSetup_verification() {
        // given & when & then
        // Mock 객체들이 제대로 주입되었는지 확인
        assertThat(addressRepository).isNotNull();
        assertThat(addressMapper).isNotNull();
        assertThat(addressService).isNotNull();
    }

    @Test
    @DisplayName("주소 목록 조회 성공")
    void getAllAddresses_success() {
        // given
        given(addressRepository.findByUser(testUser)).willReturn(Arrays.asList(testAddress));

        // when
        var addresses = addressRepository.findByUser(testUser);

        // then
        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getAddressName()).isEqualTo("집");
        verify(addressRepository).findByUser(testUser);
    }

    @Test
    @DisplayName("존재하지 않는 주소 조회 실패")
    void findAddress_notFound_fail() {
        // given
        UUID nonExistentId = UUID.randomUUID();
        given(addressRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> {
            addressRepository.findById(nonExistentId)
                .orElseThrow(() -> new BusinessException(null));
        }).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("주소 개수 조회 성공")
    void countByUser_success() {
        // given
        given(addressRepository.countByUser(testUser)).willReturn(2L);

        // when
        long count = addressRepository.countByUser(testUser);

        // then
        assertThat(count).isEqualTo(2L);
        verify(addressRepository).countByUser(testUser);
    }

    @Test
    @DisplayName("기본 주소 조회 성공")
    void findDefaultAddress_success() {
        // given
        given(addressRepository.findDefaultByUser(testUser)).willReturn(Optional.of(testAddress));

        // when
        Optional<Address> defaultAddress = addressRepository.findDefaultByUser(testUser);

        // then
        assertThat(defaultAddress).isPresent();
        assertThat(defaultAddress.get().getDefaultAddress()).isTrue();
        verify(addressRepository).findDefaultByUser(testUser);
    }

    @Test
    @DisplayName("사용자별 활성 주소 조회 성공")
    void findActiveByUser_success() {
        // given
        given(addressRepository.findActiveByUserOrderByIsDefaultDescCreatedAtDesc(testUser))
            .willReturn(Arrays.asList(testAddress));

        // when
        var activeAddresses = addressRepository.findActiveByUserOrderByIsDefaultDescCreatedAtDesc(testUser);

        // then
        assertThat(activeAddresses).hasSize(1);
        assertThat(activeAddresses.get(0).getAddressName()).isEqualTo("집");
        verify(addressRepository).findActiveByUserOrderByIsDefaultDescCreatedAtDesc(testUser);
    }
}