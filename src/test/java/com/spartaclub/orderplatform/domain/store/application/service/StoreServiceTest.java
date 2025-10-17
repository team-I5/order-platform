package com.spartaclub.orderplatform.domain.store.application.service;


import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.APPROVED;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.REJECTED;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.CATEGORY_NOT_EXIST;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.DUPLICATE_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.DUPLICATE_STORE_NAME;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_DELETE_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_MODIFY_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_REGISTER_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_UPDATE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_APPROVED_STORE_MODIFIABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_PENDING_STORE_APPROVABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_REJECTED_STORE_MODIFIABLE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.store.application.mapper.StoreMapper;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreCategory;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByKeywordRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @Mock
    private CategoryRepository categoryRepository;

    private Store store;
    private UUID storeId;
    private UUID categoryId;
    private User owner;
    private User manager;
    private StoreRequestDto storeRequestDto;
    private StoreResponseDto storeResponseDto;
    private RejectStoreRequestDto rejectStoreRequestDto;

    @BeforeEach
    void setUp() {
        manager = User.createManager("manager1", "manager1@email.com", "encodedPassword",
            "manager1", "01012345678");
        owner = User.createBusinessUser("owner1", "owner1@email.com", "encodedPassword", "owner1",
            "01099999999", UserRole.OWNER, "1234567890");

        ReflectionTestUtils.setField(owner, "userId", 1L);

        storeId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        storeRequestDto = new StoreRequestDto();
        storeRequestDto.setStoreName("MyStore");
        storeRequestDto.setStoreAddress("address1");
        storeRequestDto.setStoreNumber("01234567890");
        storeRequestDto.setStoreDescription("store1");

        rejectStoreRequestDto = new RejectStoreRequestDto();
        rejectStoreRequestDto.setRejectReason("REJECTED");

        store = Store.create(owner, storeRequestDto);

        storeResponseDto = new StoreResponseDto("MyStore", APPROVED);
    }

    @Test
    @DisplayName("음식점 생성 성공")
    void createStore_success() {
        given(storeRepository.existsByUserAndStoreName(owner, storeRequestDto.getStoreName()))
            .willReturn(false);
        given(storeRepository.save(any(Store.class))).willReturn(store);
        given(storeMapper.toStoreResponseDto(any(Store.class))).willReturn(storeResponseDto);

        StoreResponseDto result = storeService.createStore(owner, storeRequestDto);

        AssertionsForClassTypes.assertThat(result.getStoreName()).isEqualTo("MyStore");
        verify(storeRepository).save(any(Store.class));
        verify(storeMapper).toStoreResponseDto(any(Store.class));
    }

    @Test
    @DisplayName("음식점 생성 - 음식점 이름 중복")
    void createStore_duplicateName() {

        given(storeRepository.existsByUserAndStoreName(owner, storeRequestDto.getStoreName()))
            .willReturn(true);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.createStore(owner, storeRequestDto)
        );

        AssertionsForClassTypes.assertThat(exception.getErrorCode())
            .isEqualTo(DUPLICATE_STORE_NAME);
    }

    @Test
    @DisplayName("음식점 재승인 신청 성공")
    void reapplyStore_success() {
        ReflectionTestUtils.setField(store, "status", REJECTED);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeMapper.toStoreResponseDto(store)).willReturn(storeResponseDto);

        StoreResponseDto result = storeService.reapplyStore(owner, storeId, storeRequestDto);

        assertThat(store.getStatus()).isEqualTo(StoreStatus.PENDING); // 상태 변화 확인
        assertThat(result).isEqualTo(storeResponseDto);
    }

    @Test
    @DisplayName("재승인 신청 - 다른 사용자가 요청")
    void reapplyStore_notOwner() {
        // given
        ReflectionTestUtils.setField(store, "status", REJECTED);
        User otherUser = User.createBusinessUser("other", "other@email.com", "pw", "other",
            "01000000000", UserRole.OWNER, "1111111111");
        ReflectionTestUtils.setField(otherUser, "userId", 2L);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.reapplyStore(otherUser, storeId, storeRequestDto));

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_UPDATE);
    }

    @Test
    @DisplayName("재승인 신청 - 상태가 REJECTED가 아닌 경우")
    void reapplyStore_notRejected() {
        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.reapplyStore(owner, storeId, storeRequestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ONLY_REJECTED_STORE_MODIFIABLE);
    }

    @Test
    @DisplayName("승인된 음식점 수정 성공")
    void updateApprovedStore_success() {
        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(storeMapper.toStoreResponseDto(store)).willReturn(storeResponseDto);

        StoreResponseDto result = storeService.updateApprovedStore(owner, storeId, storeRequestDto);

        assertThat(result).isEqualTo(storeResponseDto);
    }

    @Test
    @DisplayName("음식점 수정 - 승인되지 않은 음식점 수정")
    void updateApprovedStore_notApproved() {
        ReflectionTestUtils.setField(store, "status", StoreStatus.PENDING);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.updateApprovedStore(owner, storeId, storeRequestDto));

        assertThat(exception.getErrorCode()).isEqualTo(ONLY_APPROVED_STORE_MODIFIABLE);
    }

    @Test
    @DisplayName("음식점 수정 - 소유하지 않은 음식점 수정")
    void updateApprovedStore_notOwner() {
        // given
        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);
        User otherUser = User.createBusinessUser("other", "other@email.com", "pw", "other",
            "01011112222", UserRole.OWNER, "0987654321");
        ReflectionTestUtils.setField(otherUser, "userId", 2L);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.updateApprovedStore(otherUser, storeId, storeRequestDto));

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_UPDATE);
    }

    @Test
    @DisplayName("음식점 승인 성공")
    void approveStore_success() {
        Store pendingStore = mock(Store.class);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(pendingStore));
        given(pendingStore.getStatus()).willReturn(StoreStatus.PENDING);
        given(storeMapper.toStoreResponseDto(any(Store.class))).willReturn(storeResponseDto);

        storeService.approveStore(storeId);

        verify(pendingStore).approve();
        verify(storeMapper).toStoreResponseDto(any(Store.class));
    }

    @Test
    @DisplayName("음식점 승인 - 승인 불가 상태")
    void approveStore_notPending() {
        Store notPendingStore = mock(Store.class);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(notPendingStore));
        given(notPendingStore.getStatus()).willReturn(APPROVED);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.approveStore(storeId));

        AssertionsForClassTypes.assertThat(exception.getErrorCode())
            .isEqualTo(ONLY_PENDING_STORE_APPROVABLE);
    }

    @Test
    @DisplayName("음식점 승인 거절 성공")
    void rejectStore_success() {
        Store pendingStore = mock(Store.class);
        RejectStoreRequestDto rejectDto = new RejectStoreRequestDto();
        rejectDto.setRejectReason("REJECTED");

        RejectStoreResponseDto responseDto = new RejectStoreResponseDto("MyStore", REJECTED,
            "REJECTED");

        given(storeRepository.findById(storeId)).willReturn(Optional.of(pendingStore));
        given(pendingStore.getStatus()).willReturn(StoreStatus.PENDING);
        given(storeMapper.toRejectStoreResponseDto(pendingStore)).willReturn(responseDto);

        RejectStoreResponseDto result = storeService.rejectStore(storeId, rejectDto);

        verify(pendingStore).reject("REJECTED"); // store.reject 호출 확인
        assertThat(result).isEqualTo(responseDto); // mapper 반환 확인
    }

    @Test
    @DisplayName("음식점 삭제 성공")
    void deleteStore_success() {
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        storeService.deleteStore(owner, storeId);

        assertThat(store.isDeleted()).isTrue();
        verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void addCategoryToStore_success() {
        Category category = Category.of("KOREAN");
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);

        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(categoryId));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        StoreCategoryResponseDto responseDto = new StoreCategoryResponseDto(store.getStoreName(),
            List.of("KOREAN"));
        given(storeMapper.toStoreCategoryResponseDto(store)).willReturn(responseDto);

        StoreCategoryResponseDto result = storeService.addCategoryToStore(storeId, owner, dto);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("카테고리 등록 - 중복 카테고리")
    void addCategoryToStore_duplicateCategory() {
        Category category = Category.of("KOREAN");
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        StoreCategory storeCategory = mock(StoreCategory.class);
        given(storeCategory.getCategory()).willReturn(category);
        given(storeCategory.isDeleted()).willReturn(false);

        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);

        store.getStoreCategories().add(storeCategory);

        StoreCategoryRequestDto categoryDto = new StoreCategoryRequestDto();
        categoryDto.setCategoryIds(List.of(categoryId));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.addCategoryToStore(storeId, owner, categoryDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATE_CATEGORY);
    }

    @Test
    @DisplayName("카테고리 등록 - 승인되지 않은 음식점에 카테고리 등록")
    void addCategoryToStore_storeNotApproved() {
        ReflectionTestUtils.setField(store, "status", StoreStatus.PENDING);

        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(UUID.randomUUID()));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.addCategoryToStore(storeId, owner, dto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ONLY_APPROVED_STORE_MODIFIABLE);
    }

    @Test
    @DisplayName("카테고리 등록 - 다른 사용자가 음식점에 카테고리 등록")
    void addCategoryToStore_notOwnedStore() {
        User otherUser = User.createBusinessUser("other", "other@email.com", "pw", "other",
            "01000000000", UserRole.OWNER, "1111111111");
        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);

        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(UUID.randomUUID()));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> storeService.addCategoryToStore(storeId, otherUser, dto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_REGISTER_CATEGORY);
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategoryToStore_success() {
        UUID newCategoryId = UUID.randomUUID();

        Category existingCategory = Category.of("KOREAN");
        ReflectionTestUtils.setField(existingCategory, "categoryId", categoryId);
        StoreCategory existingStoreCategory = mock(StoreCategory.class);
        given(existingStoreCategory.getDeletedId()).willReturn(null);
        given(existingStoreCategory.getCategory()).willReturn(existingCategory);

        ReflectionTestUtils.setField(store, "status", StoreStatus.APPROVED);
        store.getStoreCategories().add(existingStoreCategory);

        Category newCategory = Category.of("JAPANESE");
        ReflectionTestUtils.setField(newCategory, "categoryId", newCategoryId);
        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(newCategoryId));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(newCategoryId)).willReturn(Optional.of(newCategory));

        StoreCategoryResponseDto responseDto = new StoreCategoryResponseDto(store.getStoreName(),
            List.of("JAPANESE"));
        given(storeMapper.toStoreCategoryResponseDto(store)).willReturn(responseDto);

        StoreCategoryResponseDto result = storeService.updateCategoryToStore(storeId, owner, dto);

        verify(existingStoreCategory).scSoftDelete(owner.getUserId());
        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("카테고리 수정 - 권한 없음")
    void updateCategoryToStore_notOwner() {
        User otherUser = User.createBusinessUser("other", "other@email.com", "pwd", "other",
            "01011111111", UserRole.OWNER, "9999999999");
        ReflectionTestUtils.setField(otherUser, "userId", 2L);

        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(UUID.randomUUID()));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.updateCategoryToStore(storeId, otherUser, dto));

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_MODIFY_CATEGORY);
    }

    @Test
    @DisplayName("키워드로 승인된 가게 검색 성공")
    void searchStoreListByKeyword_success() {
        StoreSearchByKeywordRequestDto dto = new StoreSearchByKeywordRequestDto();
        dto.setStoreName("MyStore");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Store> stores = new PageImpl<>(List.of(store));

        given(storeRepository.findApprovedStoresByStoreName("MyStore", APPROVED, pageable))
            .willReturn(stores);
        given(storeMapper.toStoreSearchResponseDto(any(Store.class)))
            .willReturn(new StoreSearchResponseDto("MyStore", 4.5, 10, List.of("KOREAN")));

        Page<StoreSearchResponseDto> result = storeService.searchStoreListByKeyword(dto, pageable);

        assertThat(result.getContent().get(0).getStoreName()).isEqualTo("MyStore");
        verify(storeRepository).findApprovedStoresByStoreName("MyStore", APPROVED, pageable);
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategoryFromStore_success() {
        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(categoryId));

        Store mockStore = mock(Store.class);
        given(mockStore.getUser()).willReturn(owner);

        Category category = Category.of("KOREAN");
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(mockStore));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        storeService.deleteCategoryFromStore(storeId, owner, dto);

        verify(mockStore).removeCategory(owner.getUserId(), category);
    }

    @Test
    @DisplayName("카테고리 삭제 - 가게 소유자가 아니면 예외 발생")
    void deleteCategoryFromStore_notOwner() {
        Category category = Category.of("KOREAN");
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(categoryId));

        User otherUser = User.createBusinessUser("other", "other@email.com",
            "pw", "other", "01000000000", UserRole.OWNER, "9999999999");
        ReflectionTestUtils.setField(otherUser, "userId", 2L);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.deleteCategoryFromStore(storeId, otherUser, dto));

        assertThat(exception.getErrorCode()).isEqualTo(NOT_OWNED_STORE_TO_DELETE_CATEGORY);
    }

    @Test
    @DisplayName("카테고리 삭제 - 카테고리 존재하지 않으면 예외 발생")
    void deleteCategoryFromStore_categoryNotExist() {
        StoreCategoryRequestDto dto = new StoreCategoryRequestDto();
        dto.setCategoryIds(List.of(categoryId));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
            () -> storeService.deleteCategoryFromStore(storeId, owner, dto));

        assertThat(exception.getErrorCode()).isEqualTo(CATEGORY_NOT_EXIST);
    }
}