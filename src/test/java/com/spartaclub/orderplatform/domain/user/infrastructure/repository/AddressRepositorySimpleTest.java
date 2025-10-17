package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * AddressRepository 간단한 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AddressRepositorySimpleTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("주소 저장 및 조회 테스트")
    void saveAndFind_success() {
        // given
        User user = User.createUser(
            "addrtest1",
            "addr1@example.com", 
            "encodedPassword",
            "nick1",
            "01012345678",
            UserRole.CUSTOMER
        );
        user = userRepository.save(user);

        Address address = Address.builder()
            .addressName("집")
            .name("홍길동")
            .phoneNumber("01012345678")
            .postCode("12345")
            .roadNameAddress("서울시 강남구")
            .detailedAddress("456호")
            .defaultAddress(true)
            .user(user)
            .build();

        // when
        Address savedAddress = addressRepository.save(address);
        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getAddressId());

        // then
        assertThat(foundAddress).isPresent();
        assertThat(foundAddress.get().getAddressName()).isEqualTo("집");
        assertThat(foundAddress.get().getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("사용자별 주소 조회 테스트")
    void findByUser_success() {
        // given
        User user = User.createUser(
            "addrtest2",
            "addr2@example.com",
            "encodedPassword", 
            "nick2",
            "01087654321",
            UserRole.CUSTOMER
        );
        user = userRepository.save(user);

        Address address = Address.builder()
            .addressName("회사")
            .name("홍길동")
            .phoneNumber("01087654321")
            .postCode("54321")
            .roadNameAddress("서울시 서초구")
            .detailedAddress("789호")
            .defaultAddress(false)
            .user(user)
            .build();
        addressRepository.save(address);

        // when
        var addresses = addressRepository.findByUser(user);

        // then
        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getAddressName()).isEqualTo("회사");
    }

    @Test
    @DisplayName("주소 개수 조회 테스트")
    void countByUser_success() {
        // given  
        User user = User.createUser(
            "addrtest3",
            "addr3@example.com",
            "encodedPassword",
            "nick3", 
            "01011111111",
            UserRole.CUSTOMER
        );
        user = userRepository.save(user);

        Address address1 = Address.builder()
            .addressName("집")
            .name("홍길동")
            .phoneNumber("01011111111")
            .postCode("11111")
            .roadNameAddress("서울시 강남구")
            .detailedAddress("111호")
            .defaultAddress(true)
            .user(user)
            .build();
        
        Address address2 = Address.builder()
            .addressName("회사")
            .name("홍길동")
            .phoneNumber("01011111111")
            .postCode("22222")
            .roadNameAddress("서울시 서초구")
            .detailedAddress("222호")
            .defaultAddress(false)
            .user(user)
            .build();

        addressRepository.save(address1);
        addressRepository.save(address2);

        // when
        long count = addressRepository.countByUser(user);

        // then
        assertThat(count).isEqualTo(2);
    }
}