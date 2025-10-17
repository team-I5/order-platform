//package com.spartaclub.orderplatform.domain.store.application.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//import com.spartaclub.orderplatform.domain.store.domain.model.Store;
//import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
//import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
//import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
//import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
//import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
//import com.spartaclub.orderplatform.domain.user.domain.entity.User;
//import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
//import com.spartaclub.orderplatform.global.exception.BusinessException;
//import java.util.UUID;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class StoreSearchTest {
//
//    @Autowired
//    private StoreService storeService;
//
//    @Autowired
//    private StoreRepository storeRepository;
//
//    private User owner;
//    private User manager;
//    private User customer;
//
//    private StoreRequestDto requestDto;
//
//
//    @BeforeEach
//    void setUp() {
//        owner = new User();
//        owner.setUserId(1L);
//        owner.setUsername("owner1");
//        owner.setEmail("owner@example.com");
//        owner.setPassword("encodedPassword");
//        owner.setNickname("오너");
//        owner.setPhoneNumber("01012345678");
//        owner.setRole(UserRole.OWNER);
//
//        manager = new User();
//        manager.setUserId(2L);
//        manager.setUsername("manager1");
//        manager.setEmail("manager@example.com");
//        manager.setPassword("encodedPassword");
//        manager.setNickname("매니저");
//        manager.setPhoneNumber("01087654321");
//        manager.setRole(UserRole.MANAGER);
//
//        customer = new User();
//        customer.setUserId(3L);
//        customer.setUsername("customer1");
//        customer.setEmail("customer@example.com");
//        customer.setPassword("encodedPassword");
//        customer.setNickname("손님");
//        customer.setPhoneNumber("01011112222");
//        customer.setRole(UserRole.CUSTOMER);
//
//        requestDto = new StoreRequestDto();
//        requestDto.setStoreName("Test Store " + UUID.randomUUID().toString().substring(0, 8));
//        requestDto.setStoreAddress("서울시 강남구");
//        requestDto.setStoreNumber("02" + UUID.randomUUID().toString().substring(0, 8));
//        requestDto.setStoreDescription("Test Description");
//    }
//
//    @Test
//    @Transactional
//    @WithMockUser(username = "customer1", roles = {"CUSTOMER"})
//    @DisplayName("Customer는 승인된 음식점만 조회 가능")
//    void searchStore_customer() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//        store.approve();
//
//        StoreSearchRequestDto searchDto = new StoreSearchRequestDto();
//        Page<StoreSearchResponseDto> page = storeService.searchStore(searchDto, customer);
//
//        assertThat(page.getContent()).allSatisfy(s -> assertThat(s.getStoreName()).isNotNull());
//        assertThat(page.getTotalElements()).isEqualTo(1);
//    }
//
//    @Test
//    @Transactional
//    @WithMockUser(username = "owner1", roles = {"OWNER"})
//    @DisplayName("Owner는 본인 소유 음식점의 목록만 조회 가능")
//    void searchStoreDetail_owner() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository
//            .findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")))
//            .getContent().get(0);
//
//        store.approve();
//
//        StoreDetailResponseDto response =
//            storeService.searchStoreDetail(store.getStoreId(), owner, UserRole.OWNER);
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStoreName()).isEqualTo(requestDto.getStoreName());
//
//        User anotherOwner = new User();
//        anotherOwner.setUserId(99L);
//        anotherOwner.setRole(UserRole.OWNER);
//
//        assertThatThrownBy(() ->
//            storeService
//                .searchStoreDetail(store.getStoreId(), anotherOwner, UserRole.OWNER))
//            .isInstanceOf(BusinessException.class);
//    }
//
//
//    @Test
//    @Transactional
//    @WithMockUser(username = "manager1", roles = {"MANAGER"})
//    @DisplayName("Manager는 모든 음식점의 목록을 조회 가능")
//    void searchStoreDetail_manager() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository
//            .findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")))
//            .getContent().get(0);
//        store.approve();
//
//        StoreDetailResponseDto response =
//            storeService.searchStoreDetail(store.getStoreId(), manager, UserRole.MANAGER);
//
//        assertThat(response).isNotNull();
//        assertThat(response.getStoreName()).isEqualTo(requestDto.getStoreName());
//    }
//
//    @Test
//    @WithMockUser(username = "customer1", roles = {"CUSTOMER"})
//    @DisplayName("CUSTOMER는 승인된 음식점의 상세 정보만 조회 가능")
//    void searchStoreDetail_customer_approvedOnly() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//
//        store.approve();
//        StoreDetailResponseDto dto = storeService.searchStoreDetail(
//            store.getStoreId(), customer, UserRole.CUSTOMER
//        );
//
//        assertThat(dto.getStoreName()).isEqualTo(store.getStoreName());
//    }
//
//    @Test
//    @WithMockUser(username = "owner1", roles = {"OWNER"})
//    @DisplayName("OWNER의 자신의 음식점 상세 정보 조회")
//    void searchStoreDetail_owner_ownStore() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//
//        StoreDetailResponseDto dto = storeService.searchStoreDetail(
//            store.getStoreId(), owner, UserRole.OWNER
//        );
//
//        assertThat(dto.getStoreName()).isEqualTo(store.getStoreName());
//    }
//
//    @Test
//    @WithMockUser(username = "owner1", roles = {"OWNER"})
//    @DisplayName("OWNER는 다른 OWNER의 음식점 상세 정보 조회 불가")
//    void searchStoreDetail_owner_otherStore() {
//        storeService.createStore(owner, requestDto);
//
//        User otherOwner = new User();
//        otherOwner.setUserId(2L);
//        otherOwner.setRole(UserRole.OWNER);
//
//        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//
//        assertThatThrownBy(() ->
//            storeService.searchStoreDetail(store.getStoreId(), otherOwner, UserRole.OWNER)
//        ).isInstanceOf(BusinessException.class);
//    }
//
//    @Test
//    @WithMockUser(username = "manager1", roles = {"MANAGER"})
//    @DisplayName("MANAGER는 모든 음식점 상세 정보 조회 가능")
//    void searchStoreDetail_manager_allStores() {
//        storeService.createStore(owner, requestDto);
//        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//
//        StoreDetailResponseDto dto = storeService.searchStoreDetail(
//            store.getStoreId(), manager, UserRole.MANAGER
//        );
//
//        assertThat(dto.getStoreName()).isEqualTo(store.getStoreName());
//    }
//}
