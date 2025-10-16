package com.spartaclub.orderplatform.domain.store.application.service;

import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.APPROVED;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.PENDING;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.REJECTED;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.CATEGORY_NOT_EXIST;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.DUPLICATE_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.DUPLICATE_STORE_NAME;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_EXIST;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_DELETE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_DELETE_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_MODIFY_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_REGISTER_CATEGORY;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NOT_OWNED_STORE_TO_UPDATE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.NO_PERMISSION;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_APPROVED_STORE_MODIFIABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_PENDING_STORE_APPROVABLE;
import static com.spartaclub.orderplatform.domain.store.exception.StoreErrorCode.ONLY_REJECTED_STORE_MODIFIABLE;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.store.application.mapper.StoreMapper;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreCategory;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByKeywordRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    private final StoreMapper storeMapper;

    // Owner의 음식점 생성
    @Transactional
    public StoreResponseDto createStore(User user, StoreRequestDto dto) {
        // 이미 나에게 존재하는 가게 이름인지 확인
        boolean existStoreName = storeRepository
            .existsByUserAndStoreName(user, dto.getStoreName());

        if (existStoreName) {
            log.warn("[Store] duplicate store name. Store name: {}", dto.getStoreName());
            throw new BusinessException(DUPLICATE_STORE_NAME);
        }

        Store store = Store.create(user, dto);

        return storeMapper.toStoreResponseDto(storeRepository.save(store));
    }

    // Owner의 음식점 재승인 신청
    @Transactional
    public StoreResponseDto reapplyStore(User user, UUID storeId, StoreRequestDto dto) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_UPDATE);
        }

        if (store.getStatus() != REJECTED) {
            log.warn("[Store] not rejected");
            throw new BusinessException(ONLY_REJECTED_STORE_MODIFIABLE);
        }

        store.updateStoreInfo(dto);
        store.requestReapproval();

        return storeMapper.toStoreResponseDto(store);
    }

    // 승인된 음식점의 기본정보 수정
    @Transactional
    public StoreResponseDto updateApprovedStore(
        User user, UUID storeId, StoreRequestDto dto
    ) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_UPDATE);
        }

        if (store.getStatus() != APPROVED) {
            log.warn("[Store] not approved");
            throw new BusinessException(ONLY_APPROVED_STORE_MODIFIABLE);
        }

        store.updateStoreInfo(dto);

        return storeMapper.toStoreResponseDto(store);
    }

    // Owner의 음식점 삭제
    @Transactional
    public void deleteStore(User user, UUID storeId) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_DELETE);
        }

        store.delete(user.getUserId());
        store.storeSoftDelete(user.getUserId());
    }

    // Manager의 음식점 승인
    @Transactional
    public StoreResponseDto approveStore(UUID storeId) {
        Store store = getStore(storeId);

        checkStatus(store);

        store.approve();

        return storeMapper.toStoreResponseDto(store);
    }


    // Manager의 음식점 승인 거절
    @Transactional
    public RejectStoreResponseDto rejectStore(UUID storeId, RejectStoreRequestDto dto) {
        Store store = getStore(storeId);

        checkStatus(store);

        store.reject(dto.getRejectReason());

        return storeMapper.toRejectStoreResponseDto(store);
    }

    /*
     *  음식점 목록 조회
     *    Role별로 목록 내용 다름
     *      - customer: 삭제되지 않은 승인된 음식점
     *      - owner: 자신의 음식점
     *      - manager/master: 전체 음식점, 승인 상태별 / 음식점 주인별
     */
    @Transactional(readOnly = true)
    public Page<StoreSearchResponseDto> searchStore(
        StoreSearchRequestDto dto, User user, Pageable pageable
    ) {
        return switch (user.getRole()) {
            case CUSTOMER -> searchStoreForCustomer(pageable);
            case OWNER -> searchStoreForOwner(user, pageable);
            case MANAGER, MASTER -> searchStoreForAdmin(dto, pageable);
        };
    }

    // 음식점 상세 조회
    @Transactional(readOnly = true)
    public StoreDetailResponseDto searchStoreDetail(UUID storeId, User user, UserRole role) {
        Store store = getStore(storeId);

        switch (role) {
            case CUSTOMER -> {
                if (store.getStatus() != APPROVED) {
                    log.warn("[Store] no permission");
                    throw new BusinessException(NO_PERMISSION);
                }
            }
            case OWNER -> {
                if (!store.getUser().getUserId().equals(user.getUserId())) {
                    log.warn("[Store] no permission");
                    throw new BusinessException(NO_PERMISSION);
                }
            }
            case MANAGER, MASTER -> {
                // 모든 음식점 조회 가능
            }
        }

        return storeMapper.toStoreDetailResponseDto(store, role);
    }

    // 음식점 카테고리 등록
    @Transactional
    public StoreCategoryResponseDto addCategoryToStore(
        UUID storeId, User user, StoreCategoryRequestDto dto
    ) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_REGISTER_CATEGORY);
        }

        if (store.getStatus() != APPROVED) {
            log.warn("[Store] not approved");
            throw new BusinessException(ONLY_APPROVED_STORE_MODIFIABLE);
        }

        checkCategory(dto, store);

        return storeMapper.toStoreCategoryResponseDto(store);
    }

    // 음식점 카테고리 수정
    @Transactional
    public StoreCategoryResponseDto updateCategoryToStore(
        UUID storeId, User user, StoreCategoryRequestDto dto
    ) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_MODIFY_CATEGORY);
        }

        // 기존 관계들은 soft deleteProductOptionGroup 처리
        store.getStoreCategories().forEach(storeCategory -> {
            if (storeCategory.getDeletedId() == null) {
                storeCategory.scSoftDelete(user.getUserId());
            }
        });

        checkCategory(dto, store);

        return storeMapper.toStoreCategoryResponseDto(store);
    }

    // 음식점 카테고리 제거
    @Transactional
    public void deleteCategoryFromStore(UUID storeId, User user, StoreCategoryRequestDto dto) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            log.warn("[Store] does not own this store");
            throw new BusinessException(NOT_OWNED_STORE_TO_DELETE_CATEGORY);
        }

        for (UUID categoryId : dto.getCategoryIds()) {

            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                        log.warn("[Store] category not exist");
                        return new BusinessException(CATEGORY_NOT_EXIST);
                    }
                );
            store.removeCategory(user.getUserId(), category);
        }
    }

    // 음식점 카테고리별 목록 조회
    @Transactional(readOnly = true)
    public Page<StoreSearchResponseDto> searchStoreByCategory(
        StoreSearchByCategoryRequestDto dto, User user, Pageable pageable
    ) {
        Page<Store> stores;

        switch (user.getRole()) {
            case CUSTOMER -> stores = storeRepository
                .findApprovedStoreByCategory(dto.getCategoryType(), pageable);
            case OWNER -> stores = storeRepository
                .findOwnerApprovedStoreByCategory(
                    dto.getCategoryType(), user.getUserId(), pageable
                );
            case MANAGER, MASTER -> stores = storeRepository
                .findAllStoreByCategory(dto.getCategoryType(), pageable);
            default -> {
                log.warn("[Store] no permission");
                throw new BusinessException(NO_PERMISSION);
            }
        }

        return stores.map(this::getStoreSearchByCategoryResponseDto);
    }

    // 해당 키워드가 들어가는 식당 목록
    @Transactional
    public Page<StoreSearchResponseDto> searchStoreListByKeyword(
        StoreSearchByKeywordRequestDto dto, Pageable pageable
    ) {
        Page<Store> stores = storeRepository
            .findApprovedStoresByStoreName(dto.getStoreName(), APPROVED, pageable);

        return stores.map(storeMapper::toStoreSearchResponseDto);
    }

    // mapper에서 분리한 로직 - 유효한 카테고리만 포함한 dto 변환
    private StoreSearchResponseDto getStoreSearchByCategoryResponseDto(Store store) {
        StoreSearchResponseDto responseDto
            = storeMapper.toStoreSearchResponseDto(store);

        List<String> categories = store.getStoreCategories().stream()
            .filter(storeCategory ->
                storeCategory.getCategory() != null && !storeCategory.isDeleted()
            )
            .map(StoreCategory::getCategory)
            .filter(category -> !category.isDeleted())
            .map(Category::getType)
            .toList();

        return new StoreSearchResponseDto(
            responseDto.getStoreName(),
            responseDto.getAverageRating(),
            responseDto.getReviewCount(),
            categories
        );
    }

    // 존재하는 음식점인지 확인
    public Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> {
                log.warn("[Store] not exist");
                return new BusinessException(NOT_EXIST);
            });
    }

    // 승인 대기 상태 확인
    private static void checkStatus(Store store) {
        if (store.getStatus() != PENDING) {
            log.warn("[Store] not pending");
            throw new BusinessException(ONLY_PENDING_STORE_APPROVABLE);
        }
    }

    // 존재하는 카테고리인지, 이미 등록된 카테고리인지 확인
    private void checkCategory(StoreCategoryRequestDto dto, Store store) {
        for (UUID categoryId : dto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("[Store] category not exist");
                    return new BusinessException(CATEGORY_NOT_EXIST);
                });

            boolean exist = store.getStoreCategories().stream()
                .anyMatch(storeCategory ->
                    storeCategory.getCategory().getCategoryId().equals(categoryId)
                        && !storeCategory.isDeleted()
                );

            if (exist) {
                log.warn("[Store] duplicate category. category: {}", category.getType());
                throw new BusinessException(DUPLICATE_CATEGORY);
            }

            store.addCategory(category);
        }
    }

    // Customer의 음식점 목록 조회 - 승인 상태, 삭제 여부 확인
    private Page<StoreSearchResponseDto> searchStoreForCustomer(Pageable pageable) {
        return storeRepository.findByStatusAndDeletedAtIsNull(APPROVED, pageable)
            .map(storeMapper::toStoreSearchResponseDto);
    }

    // Owner의 음식점 목록 조회 - 본인 소유 음식점인지 확인
    private Page<StoreSearchResponseDto> searchStoreForOwner(User user, Pageable pageable) {
        return storeRepository.findByUser(user, pageable)
            .map(storeMapper::toStoreSearchResponseDto);
    }

    // Manager, Master의 음식점 목록 조회 - 상태별, owner별, 전체 조회
    private Page<StoreSearchResponseDto> searchStoreForAdmin(
        StoreSearchRequestDto dto, Pageable pageable
    ) {
        if (dto.getStatus() != null && dto.getOwnerId() != null) {
            return storeRepository
                .findByStatusAndUser_UserId(dto.getStatus(), dto.getOwnerId(), pageable)
                .map(storeMapper::toStoreSearchResponseDto);
        } else if (dto.getStatus() != null) {
            return storeRepository.findByStatus(dto.getStatus(), pageable)
                .map(storeMapper::toStoreSearchResponseDto);
        } else if (dto.getOwnerId() != null) {
            return storeRepository.findByUser_UserId(dto.getOwnerId(), pageable)
                .map(storeMapper::toStoreSearchResponseDto);
        } else {
            return storeRepository.findAll(pageable)
                .map(storeMapper::toStoreSearchResponseDto);
        }
    }
}
