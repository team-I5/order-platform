package com.spartaclub.orderplatform.domain.store.application.service;

import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.APPROVED;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.PENDING;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.REJECTED;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import com.spartaclub.orderplatform.domain.category.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.store.application.mapper.StoreMapper;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreCategory;
import com.spartaclub.orderplatform.domain.store.infrastructure.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchByCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.domain.entity.UserRole;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
            throw new IllegalArgumentException("이미 같은 이름의 음식점이 존재합니다.");
        }

        Store store = storeMapper.toCreateStoreEntity(user, dto);

        return storeMapper.toStoreResponseDto(storeRepository.save(store));
    }

    // Owner의 음식점 재승인 신청
    @Transactional
    public StoreResponseDto reapplyStore(User user, UUID storeId, StoreRequestDto dto) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인의 음식점만 수정할 수 있습니다.");
        }

        if (store.getStatus() != REJECTED) {
            throw new IllegalArgumentException("승인 거절된 가게만 수정할 수 있습니다.");
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
            throw new IllegalArgumentException("본인의 음식점만 수정할 수 있습니다.");
        }

        if (store.getStatus() != APPROVED) {
            throw new IllegalArgumentException("승인된 가게만 수정할 수 있습니다.");
        }

        store.updateStoreInfo(dto);

        return storeMapper.toStoreResponseDto(store);
    }

    // Owner의 음식점 삭제
    @Transactional
    public void deleteStore(User user, UUID storeId) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인의 음식점만 삭제할 수 있습니다.");
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
    public Page<StoreSearchResponseDto> searchStore(StoreSearchRequestDto dto, User user) {
        dto.validatePageSize();

        Pageable pageable = PageRequest.of(
            dto.getPage(), dto.getSize(),
            Sort.by(DESC, "createdAt")
        );

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
                    throw new RuntimeException("권한이 없습니다.");
                }
            }
            case OWNER -> {
                if (!store.getUser().getUserId().equals(user.getUserId())) {
                    throw new RuntimeException("권한이 없습니다.");
                }
            }
            case MANAGER, MASTER -> {
                // 모든 음식점 조회 가능
            }
            default -> throw new RuntimeException("권한이 없습니다.");
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
            throw new IllegalArgumentException("본인의 음식점의 카테고리만 등록할 수 있습니다.");
        }

        if (store.getStatus() != APPROVED) {
            throw new IllegalArgumentException("승인된 가게만 카테고리를 등록할 수 있습니다.");
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
            throw new IllegalArgumentException("본인의 음식점의 카테고리만 수정할 수 있습니다.");
        }

        // 기존 관계들은 soft deleteProductOptionGroup 처리
        store.getStoreCategories().forEach(storeCategory -> {
            if (storeCategory.getDeletedId() != null) {
                storeCategory.scSoftDelete(user.getUserId());
                storeCategory.delete();
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
            throw new IllegalArgumentException("본인의 음식점의 카테고리만 삭제할 수 있습니다.");
        }

        for (UUID categoryId : dto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
            store.removeCategory(user.getUserId(), category);
        }
    }

    // 음식점 카테고리별 목록 조회
    @Transactional(readOnly = true)
    public Page<StoreSearchByCategoryResponseDto> searchStoreByCategory(
        StoreSearchByCategoryRequestDto dto, User user
    ) {
        dto.validatePageSize();

        Pageable pageable = PageRequest.of(
            dto.getPage(), dto.getSize(),
            Sort.by(DESC, "createdAt")
        );

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
            default -> throw new RuntimeException("권한이 없습니다.");
        }

        return stores.map(this::getStoreSearchByCategoryResponseDto);
    }

    // mapper에서 분리한 로직 - 유효한 카테고리만 포함한 dto 변환
    private StoreSearchByCategoryResponseDto getStoreSearchByCategoryResponseDto(Store store) {
        StoreSearchByCategoryResponseDto responseDto
            = storeMapper.toStoreSearchByCategoryResponseDto(store);

        List<Category> categories = store.getStoreCategories().stream()
            .filter(storeCategory ->
                storeCategory.getCategory() != null && !storeCategory.isDeleted()
            )
            .map(StoreCategory::getCategory)
            .filter(category -> !category.isDeleted())
            .toList();

        return new StoreSearchByCategoryResponseDto(
            responseDto.getStoreName(),
            responseDto.getAverageRating(),
            responseDto.getReviewCount(),
            categories
        );
    }

    // 존재하는 음식점인지 확인
    private Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 음식점 입니다."));
    }

    // 승인 대기 상태 확인
    private static void checkStatus(Store store) {
        if (store.getStatus() != PENDING) {
            throw new IllegalArgumentException(
                "승인 대기 상태의 가게만 승인 상태를 변경할 수 있습니다.");
        }
    }

    // 존재하는 카테고리인지, 이미 등록된 카테고리인지 확인
    private void checkCategory(StoreCategoryRequestDto dto, Store store) {
        for (UUID categoryId : dto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));

            boolean exist = store.getStoreCategories().stream()
                .anyMatch(storeCategory ->
                    storeCategory.getCategory().getCategoryId().equals(categoryId)
                        && !storeCategory.isDeleted()
                );

            if (exist) {
                throw new IllegalArgumentException("이미 등록된 카테고리 입니다." + category.getType().name());
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
