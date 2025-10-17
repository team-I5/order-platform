package com.spartaclub.orderplatform.domain.user.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Address 도메인 엔티티 테스트
 * - 주소 생성 및 수정 로직 검증
 * - 기본 주소 설정 로직 테스트
 * - 주소 데이터 무결성 검증
 */
class AddressTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.createUser("testuser", "test@example.com", "password", 
                                  "testnick", "01012345678", UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("주소 생성 - Builder 패턴")
    void createAddress_withBuilder_success() {
        // given
        UUID addressId = UUID.randomUUID();
        String addressName = "집";
        String name = "홍길동";
        String phoneNumber = "01012345678";
        String postCode = "12345";
        String roadNameAddress = "서울시 강남구 테헤란로 123";
        String detailedAddress = "456호";
        Boolean defaultAddress = true;

        // when
        Address address = Address.builder()
                .addressId(addressId)
                .addressName(addressName)
                .name(name)
                .phoneNumber(phoneNumber)
                .postCode(postCode)
                .roadNameAddress(roadNameAddress)
                .detailedAddress(detailedAddress)
                .defaultAddress(defaultAddress)
                .user(testUser)
                .build();

        // then
        assertThat(address.getAddressId()).isEqualTo(addressId);
        assertThat(address.getAddressName()).isEqualTo(addressName);
        assertThat(address.getName()).isEqualTo(name);
        assertThat(address.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(address.getPostCode()).isEqualTo(postCode);
        assertThat(address.getRoadNameAddress()).isEqualTo(roadNameAddress);
        assertThat(address.getDetailedAddress()).isEqualTo(detailedAddress);
        assertThat(address.getDefaultAddress()).isEqualTo(defaultAddress);
        assertThat(address.getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("기본 주소 설정")
    void setDefaultAddress_success() {
        // given
        Address address = Address.builder()
                .addressName("집")
                .defaultAddress(false)
                .user(testUser)
                .build();

        // when
        address.setDefaultAddress(true);

        // then
        assertThat(address.getDefaultAddress()).isTrue();
    }

    @Test
    @DisplayName("주소 정보 수정")
    void updateAddress_success() {
        // given
        Address address = Address.builder()
                .addressName("집")
                .name("홍길동")
                .phoneNumber("01012345678")
                .postCode("12345")
                .roadNameAddress("서울시 강남구 테헤란로 123")
                .detailedAddress("456호")
                .defaultAddress(true)
                .user(testUser)
                .build();

        // when
        address.setAddressName("회사");
        address.setName("김철수");
        address.setPhoneNumber("01087654321");
        address.setPostCode("54321");
        address.setRoadNameAddress("서울시 서초구 강남대로 456");
        address.setDetailedAddress("789호");
        address.setDefaultAddress(false);

        // then
        assertThat(address.getAddressName()).isEqualTo("회사");
        assertThat(address.getName()).isEqualTo("김철수");
        assertThat(address.getPhoneNumber()).isEqualTo("01087654321");
        assertThat(address.getPostCode()).isEqualTo("54321");
        assertThat(address.getRoadNameAddress()).isEqualTo("서울시 서초구 강남대로 456");
        assertThat(address.getDetailedAddress()).isEqualTo("789호");
        assertThat(address.getDefaultAddress()).isFalse();
    }

    @Test
    @DisplayName("사용자와 주소 연관관계")
    void userAddressRelation_success() {
        // given
        Address address1 = Address.builder()
                .addressName("집")
                .defaultAddress(true)
                .user(testUser)
                .build();

        Address address2 = Address.builder()
                .addressName("회사")
                .defaultAddress(false)
                .user(testUser)
                .build();

        // when & then
        assertThat(address1.getUser()).isEqualTo(testUser);
        assertThat(address2.getUser()).isEqualTo(testUser);
        assertThat(address1.getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("기본 주소 기본값 테스트")
    void defaultAddressDefaultValue_success() {
        // given & when
        Address address = Address.builder()
                .addressName("집")
                .user(testUser)
                .build();

        // then
        assertThat(address.getDefaultAddress()).isFalse(); // 기본값은 false
    }

    @Test
    @DisplayName("주소 완전한 정보 생성")
    void createCompleteAddress_success() {
        // given & when
        Address address = Address.builder()
                .addressName("본가")
                .name("홍길동")
                .phoneNumber("01012345678")
                .postCode("12345")
                .roadNameAddress("서울시 강남구 테헤란로 123")
                .detailedAddress("456호")
                .defaultAddress(true)
                .user(testUser)
                .build();

        // then
        assertThat(address.getAddressName()).isEqualTo("본가");
        assertThat(address.getName()).isEqualTo("홍길동");
        assertThat(address.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(address.getPostCode()).isEqualTo("12345");
        assertThat(address.getRoadNameAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(address.getDetailedAddress()).isEqualTo("456호");
        assertThat(address.getDefaultAddress()).isTrue();
        assertThat(address.getUser()).isEqualTo(testUser);
    }
}