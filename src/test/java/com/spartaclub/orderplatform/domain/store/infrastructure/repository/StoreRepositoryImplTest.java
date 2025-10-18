package com.spartaclub.orderplatform.domain.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class StoreRepositoryImplTest {

    @Mock
    StoreJpaRepository storeJpaRepository;

    @InjectMocks
    StoreRepositoryImpl storeRepository;

    @Test
    @DisplayName("existsByUserAndStoreName")
    void existsByUserAndStoreNameTest() {
        User user = mock(User.class);
        String storeName = "Test Store";
        given(storeJpaRepository.existsByUserAndStoreName(user, storeName)).willReturn(true);

        boolean result = storeRepository.existsByUserAndStoreName(user, storeName);

        assertThat(result).isTrue();
        then(storeJpaRepository).should(times(1)).existsByUserAndStoreName(user, storeName);
    }

    @Test
    @DisplayName("findByStatusAndDeletedAtIsNull")
    void findByStatusAndDeletedAtIsNullTest() {
        StoreStatus status = StoreStatus.APPROVED;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findByStatusAndDeletedAtIsNull(status, pageable)).willReturn(
            dummyPage);

        Page<Store> result = storeRepository.findByStatusAndDeletedAtIsNull(status, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findByStatusAndDeletedAtIsNull(status, pageable);
    }

    @Test
    @DisplayName("findByUser")
    void findByUserTest() {
        User user = mock(User.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findByUser(user, pageable)).willReturn(dummyPage);

        Page<Store> result = storeRepository.findByUser(user, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findByUser(user, pageable);
    }

    @Test
    @DisplayName("findByStatusAndUser_UserId")
    void findByStatusAndUser_UserIdTest() {
        StoreStatus status = StoreStatus.APPROVED;
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findByStatusAndUser_UserId(status, userId, pageable))
            .willReturn(dummyPage);

        Page<Store> result = storeRepository.findByStatusAndUser_UserId(status, userId, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1))
            .findByStatusAndUser_UserId(status, userId, pageable);
    }

    @Test
    @DisplayName("findByStatus")
    void findByStatusTest() {
        StoreStatus status = StoreStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findByStatus(status, pageable)).willReturn(dummyPage);

        Page<Store> result = storeRepository.findByStatus(status, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findByStatus(status, pageable);
    }

    @Test
    @DisplayName("findByUser_UserId")
    void findByUser_UserIdTest() {
        Long userId = 2L;
        Pageable pageable = PageRequest.of(1, 10);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findByUser_UserId(userId, pageable)).willReturn(dummyPage);

        Page<Store> result = storeRepository.findByUser_UserId(userId, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findByUser_UserId(userId, pageable);
    }

    @Test
    @DisplayName("findApprovedStoreByCategory")
    void findApprovedStoreByCategoryTest() {
        String categoryType = "KOREAN";
        Pageable pageable = PageRequest.of(0, 5);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findApprovedStoreByCategory(categoryType, pageable))
            .willReturn(dummyPage);

        Page<Store> result = storeRepository.findApprovedStoreByCategory(categoryType, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1))
            .findApprovedStoreByCategory(categoryType, pageable);
    }

    @Test
    @DisplayName("findOwnerApprovedStoreByCategory")
    void findOwnerApprovedStoreByCategoryTest() {
        String categoryType = "CHINESE";
        Long userId = 3L;
        Pageable pageable = PageRequest.of(0, 3);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findOwnerApprovedStoreByCategory(categoryType, userId, pageable))
            .willReturn(dummyPage);

        Page<Store> result = storeRepository
            .findOwnerApprovedStoreByCategory(categoryType, userId, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1))
            .findOwnerApprovedStoreByCategory(categoryType, userId, pageable);
    }

    @Test
    @DisplayName("findAllStoreByCategory")
    void findAllStoreByCategoryTest() {
        String categoryType = "CAFE";
        Pageable pageable = PageRequest.of(2, 10);
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findAllStoreByCategory(categoryType, pageable))
            .willReturn(dummyPage);

        Page<Store> result = storeRepository.findAllStoreByCategory(categoryType, pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1))
            .findAllStoreByCategory(categoryType, pageable);
    }

    @Test
    @DisplayName("findById")
    void findByIdTest() {
        UUID storeId = UUID.randomUUID();
        Store store = mock(Store.class);
        given(storeJpaRepository.findById(storeId)).willReturn(Optional.of(store));

        Optional<Store> found = storeRepository.findById(storeId);

        assertThat(found).containsSame(store);
        then(storeJpaRepository).should(times(1)).findById(storeId);
    }

    @Test
    @DisplayName("findAll")
    void findAllTest() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by("createdAt").descending());
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findAll(pageable)).willReturn(dummyPage);

        Page<Store> result = storeRepository.findAll(pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("findAllById")
    void findAllByIdTest() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Store store1 = mock(Store.class);
        Store store2 = mock(Store.class);
        given(store1.getStoreId()).willReturn(id1);
        given(store2.getStoreId()).willReturn(id2);

        List<Store> stores = List.of(store1, store2);
        given(storeJpaRepository.findAllById(List.of(id1, id2))).willReturn(stores);

        Map<UUID, Store> result = storeRepository.findAllById(List.of(id1, id2));

        assertThat(result).hasSize(2)
            .containsEntry(id1, store1)
            .containsEntry(id2, store2);
        then(storeJpaRepository).should(times(1)).findAllById(List.of(id1, id2));
    }
}