package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.infrastructure.repository.AddressJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductAddressReaderRepositoryImpl 단위 테스트")
class ProductAddressReaderRepositoryImplTest {

    @Mock
    private AddressJpaRepository addressJpaRepository;

    @InjectMocks
    private ProductAddressReaderRepositoryImpl productAddressReaderRepository;

    private UUID addressId;
    private Address address;

    @BeforeEach
    void setUp() {
        addressId = UUID.randomUUID();
        address = mock(Address.class);
    }

    @Nested
    @DisplayName("findById() 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("주소 ID로 Address 조회 성공 시 Optional<Address> 반환")
        void findById_success() {
            // given
            given(addressJpaRepository.findById(addressId)).willReturn(Optional.of(address));

            // when
            Optional<Address> result = productAddressReaderRepository.findById(addressId);

            // then
            assertThat(result).isPresent();
            assertThat(result).contains(address);
        }

        @Test
        @DisplayName("주소 ID로 조회 실패 시 Optional.empty() 반환")
        void findById_notFound() {
            // given
            given(addressJpaRepository.findById(addressId)).willReturn(Optional.empty());

            // when
            Optional<Address> result = productAddressReaderRepository.findById(addressId);

            // then
            assertThat(result).isEmpty();
            then(addressJpaRepository).should(times(1)).findById(addressId);
        }
    }
}
