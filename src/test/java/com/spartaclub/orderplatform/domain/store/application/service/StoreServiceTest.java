package com.spartaclub.orderplatform.domain.store.application.service;


import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.DUPLICATE_STORE_NAME;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_DELETE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_UPDATE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_APPROVED_STORE_MODIFIABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_PENDING_STORE_APPROVABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_REJECTED_STORE_MODIFIABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class StoreServiceTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    private User owner;
    private User manager;
    private StoreRequestDto requestDto;
    private StoreRequestDto updateRequestDto;
    private StoreRequestDto reapplyRequestDto;
    private StoreCategoryRequestDto storeCategoryRequestDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setUserId(1L);
        owner.setUsername("owner1");
        owner.setEmail("owner@example.com");
        owner.setPassword("encodedPassword");
        owner.setNickname("오너");
        owner.setPhoneNumber("01012345678");
        owner.setRole(UserRole.OWNER);

        manager = new User();
        manager.setUserId(100L);
        manager.setUsername("manager1");
        manager.setEmail("manager@example.com");
        manager.setPassword("encodedPassword");
        manager.setNickname("매니저");
        manager.setPhoneNumber("01087654321");
        manager.setRole(UserRole.MANAGER);

        requestDto = new StoreRequestDto();
        //requestDto.setStoreName("Test Store");
        requestDto.setStoreName("Test Store " + UUID.randomUUID().toString().substring(0, 8));
        requestDto.setStoreAddress("서울시 강남구");
        requestDto.setStoreNumber("0212345678");
        requestDto.setStoreDescription("Test Description");

        updateRequestDto = new StoreRequestDto();
        updateRequestDto.setStoreName("Update Test Store");
        updateRequestDto.setStoreAddress("서울시 성동구");
        updateRequestDto.setStoreNumber("0212341234");
        updateRequestDto.setStoreDescription("Update Test Description");

        reapplyRequestDto = new StoreRequestDto();
        reapplyRequestDto.setStoreName("Reapply Test Store");
        reapplyRequestDto.setStoreAddress("서울시 성동구");
        reapplyRequestDto.setStoreNumber("0212341234");
        reapplyRequestDto.setStoreDescription("Reapply Test Description");

        storeCategoryRequestDto = new StoreCategoryRequestDto();
        storeCategoryRequestDto.setCategoryIds(List.of(UUID.randomUUID()));
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("음식점 생성 성공")
    void createStore_success() {
        StoreResponseDto responseDto = storeService.createStore(owner, requestDto);

        assertThat(responseDto.getStatus()).isEqualTo(StoreStatus.PENDING);
        boolean exists = storeRepository.existsByUserAndStoreName(owner, requestDto.getStoreName());

        assertThat(exists).isTrue();
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("음식점 생성 실패 - 이미 같은 이름의 가게가 존재할 경우 예외 발생")
    void createStore_fail_duplicateName() {
        storeService.createStore(owner, requestDto);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.createStore(owner, requestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATE_STORE_NAME);
    }


    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인된 음식점 수정 성공")
    void updateStore_success() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        store.approve();        // 승인 상태로 변경

        StoreResponseDto responseDto = storeService
            .updateApprovedStore(owner, store.getStoreId(), updateRequestDto);

        assertThat(responseDto.getStoreName()).isEqualTo("Update Test Store");
        Store updatedStore = storeRepository.findById(store.getStoreId()).orElseThrow();
        assertThat(updatedStore.getStoreAddress()).isEqualTo("서울시 성동구");
        assertThat(updatedStore.getStoreDescription()).isEqualTo("Update Test Description");
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인된 음식점 수정 실패 - 다른 유저가 음식점 수정시 예외 발생")
    void updateStore_fail_wrongUser() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        store.approve();

        User otherOwner = new User();
        otherOwner.setUserId(2L);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.updateApprovedStore(otherOwner, store.getStoreId(), updateRequestDto)
        );
        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_UPDATE);

    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인된 음식점 수정 실패 - 승인되지 않은 음식점 수정 시 예외 발생")
    void updateStore_fail_notApproved() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        store.requestReapproval();  // 승인 상태 PENDING

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.updateApprovedStore(owner, store.getStoreId(), updateRequestDto)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ONLY_APPROVED_STORE_MODIFIABLE);
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인 거절된 음식점 재승인 성공")
    void reapplyStore_success() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        store.reject("Reject Reason");

        StoreResponseDto responseDto = storeService
            .reapplyStore(owner, store.getStoreId(), reapplyRequestDto);

        assertThat(responseDto.getStoreName()).isEqualTo("Reapply Test Store");
        Store reapplyStore = storeRepository.findById(store.getStoreId()).orElseThrow();
        assertThat(reapplyStore.getStatus()).isEqualTo(StoreStatus.PENDING);
        assertThat(reapplyStore.getStoreAddress()).isEqualTo("서울시 성동구");
        assertThat(reapplyStore.getStoreDescription()).isEqualTo("Reapply Test Description");
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인 거절된 음식점 재승인 실패 - 다른 유저가 음식점 재신청 시 예외 발생")
    void reapplyStore_fail_wrongUser() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        store.reject("Reject Reason");

        User otherOwner = new User();
        otherOwner.setUserId(2L);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.reapplyStore(otherOwner, store.getStoreId(), updateRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_UPDATE);
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("승인 거절된 음식점 재승인 실패 - 승인 거절 상태 외 재신청 시 예외 발생")
    void reapplyStore_fail_notRejectedYet() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.reapplyStore(owner, store.getStoreId(), updateRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ONLY_REJECTED_STORE_MODIFIABLE);
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("음식점 삭제 성공")
    void deleteStore_success() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        storeService.deleteStore(owner, store.getStoreId());

        Store deleteStore = storeRepository.findById(store.getStoreId()).orElseThrow();
        assertThat(deleteStore.getDeletedId()).isEqualTo(owner.getUserId());
    }

    @Test
    @WithMockUser(username = "owner1", roles = {"OWNER"})
    @DisplayName("음식점 삭제 실패 - 본인의 소유가 아닌 음식점 삭제 시 예외 발생")
    void deleteStore_fail_notOwned() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        User otherOwner = new User();
        otherOwner.setUserId(2L);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.deleteStore(otherOwner, store.getStoreId())
        );
        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_DELETE);
    }

    @Test
    @WithMockUser(username = "manager1", roles = {"MANAGER"})
    @DisplayName("매니저 음식점 승인 성공")
    void approveStore_success() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        store.requestReapproval();  // 승인 상태 PENDING

        StoreResponseDto approved = storeService.approveStore(store.getStoreId());

        assertThat(approved.getStatus()).isEqualTo(StoreStatus.APPROVED);
        Store approveStore = storeRepository.findById(store.getStoreId()).orElseThrow();
        assertThat(approveStore.getStatus()).isEqualTo(StoreStatus.APPROVED);
        assertThat(approveStore.getRejectReason()).isNull();
    }

    @Test
    @WithMockUser(username = "manager1", roles = {"MANAGER"})
    @DisplayName("매니저 음식점 승인 실패- 이미 승인된 음식점 승인 시 예외 발생")
    void approveStore_fail_alreadyApproved() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        store.approve();

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.approveStore(store.getStoreId())
        );

        assertThat(exception.getErrorCode()).isEqualTo(ONLY_PENDING_STORE_APPROVABLE);
    }

    @Test
    @WithMockUser(username = "manager1", roles = {"MANAGER"})
    @DisplayName("매니저 음식점 승인 거절 성공")
    void rejectStore_success() {
        storeService.createStore(owner, requestDto);
        Store store = storeRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);

        store.requestReapproval();  // 승인 상태 PENDING

        RejectStoreRequestDto rejectStoreRequestDto = new RejectStoreRequestDto();
        rejectStoreRequestDto.setRejectReason("reject");

        RejectStoreResponseDto rejectStoreResponseDto = storeService
            .rejectStore(store.getStoreId(), rejectStoreRequestDto);

        assertThat(rejectStoreResponseDto.getRejectReason()).isEqualTo("reject");
        Store rejectedStore = storeRepository.findById(store.getStoreId()).orElseThrow();
        assertThat(rejectedStore.getStatus()).isEqualTo(StoreStatus.REJECTED);
        assertThat(rejectedStore.getRejectReason()).isEqualTo("reject");
    }
}
