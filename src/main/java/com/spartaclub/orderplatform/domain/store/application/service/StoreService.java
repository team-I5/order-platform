package com.spartaclub.orderplatform.domain.store.application.service;


import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.APPROVED;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.PENDING;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.REJECTED;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.spartaclub.orderplatform.domain.store.application.mapper.StoreMapper;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.infrastructure.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.domain.entity.UserRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    // Owner의 음식점 생성
    public StoreResponseDto createStore(User user, StoreRequestDto dto) {
        // 이미 나에게 존재하는 가게 이름인지 확인
        boolean existStoreName = storeRepository
            .existsByUserAndStoreName(user, dto.getStoreName());
        if (existStoreName) {
            throw new IllegalArgumentException("이미 같은 이름의 음식점이 존재합니다.");
        }

        // 음식점 기본 정보로 음식점 생성
        Store store = Store.builder().user(user).storeName(dto.getStoreName())
            .storeAddress(dto.getStoreAddress()).storeNumber(dto.getStoreNumber())
            .storeDescription(dto.getStoreDescription()).status(PENDING)
            .averageRating(0.0).reviewCount(0)
            .createdId(user.getUserId()).build();

        return storeMapper.toStoreResponseDto(storeRepository.save(store));
    }

    // Owner의 음식점 재승인 신청
    public StoreResponseDto reapplyStore(User user, UUID storeId, StoreRequestDto dto) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인의 음식점만 수정할 수 있습니다.");
        }

        if (store.getStatus() != REJECTED) {
            throw new IllegalArgumentException("승인 거절된 가게만 수정할 수 있습니다.");
        }

        Store reapply = store.updateStoreInfo(user.getUserId(), dto).requestReapproval();

        return storeMapper.toStoreResponseDto(storeRepository.save(reapply));
    }

    // 승인된 음식점의 기본정보 수정
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

        Store update = store.updateStoreInfo(user.getUserId(), dto);

        return storeMapper.toStoreResponseDto(storeRepository.save(update));
    }

    // Owner의 음식점 삭제
    public void deleteStore(User user, UUID storeId) {
        Store store = getStore(storeId);

        if (!store.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인의 음식점만 삭제할 수 있습니다.");
        }

        Store deleteStore = store.toBuilder().deletedId(user.getUserId()).build();
        storeRepository.save(deleteStore);
    }

    // Manager의 음식점 승인
    public StoreResponseDto approveStore(User user, UUID storeId) {
        Store store = getStore(storeId);

        checkStatus(store);

        return storeMapper.toStoreResponseDto(
            storeRepository.save(store.approve(user.getUserId())));
    }


    // Manager의 음식점 승인 거절
    public RejectStoreResponseDto rejectStore(User user, UUID storeId,
        RejectStoreRequestDto dto) {
        Store store = getStore(storeId);

        checkStatus(store);

        return storeMapper.toRejectStoreResponseDto(
            storeRepository.save(store.reject(user.getUserId(), dto.getRejectReason())));
    }

    //음식점 목록 조회
    public Page<StoreSearchResponseDto> searchStore(
        StoreSearchRequestDto dto, UserRole role, User user
    ) {
        dto.validatePageSize();

        Pageable pageable = PageRequest.of(
            dto.getPage(), dto.getSize(),
            Sort.by(DESC, "createdAt")
        );

        Page<Store> stores;

        /**
         * Role별로 목록 내용 다름
         *  - customer: 승인된 음식점
         *  - owner: 자신의 음식점
         *  - manager/master: 전체 음식점, 승인 상태별 / 음식점 주인별
         */
        switch (role) {
            case CUSTOMER -> stores = storeRepository.findByStatus(APPROVED, pageable);
            case OWNER -> stores = storeRepository.findByUser(user, pageable);
            case MANAGER, MASTER -> {
                if (dto.getStatus() != null && user != null) {
                    stores = storeRepository.findByStatusAndUser_UserId(dto.getStatus(),
                        user.getUserId(), pageable);
                } else if (dto.getStatus() != null) {
                    stores = storeRepository.findByStatus(dto.getStatus(), pageable);
                } else if (user != null) {
                    stores = storeRepository.findByUser_UserId(user.getUserId(), pageable);
                } else {
                    stores = storeRepository.findAll(pageable);
                }
            }
            default -> stores = Page.empty();
        }
        return stores.map(storeMapper::toStoreSearchResponseDto);
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
}
