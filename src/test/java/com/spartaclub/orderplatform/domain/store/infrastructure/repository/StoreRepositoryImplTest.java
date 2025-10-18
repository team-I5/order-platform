package com.spartaclub.orderplatform.domain.store.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
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
    @DisplayName("findById 테스트")
    void findById_delegatesToJpaRepository() {
        UUID storeId = UUID.randomUUID();
        Store store = mock(Store.class);
        given(storeJpaRepository.findById(storeId)).willReturn(Optional.of(store));

        Optional<Store> found = storeRepository.findById(storeId);

        assertThat(found).containsSame(store);
        then(storeJpaRepository).should(times(1)).findById(storeId);
    }

    @Test
    @DisplayName("findAll")
    void findAll_passesPageable() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by("createdAt").descending());
        Page<Store> dummyPage = new PageImpl<>(List.of(mock(Store.class)));
        given(storeJpaRepository.findAll(pageable)).willReturn(dummyPage);

        Page<Store> result = storeRepository.findAll(pageable);

        assertThat(result).isSameAs(dummyPage);
        then(storeJpaRepository).should(times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("findAllById")
    void findAllById_returnsMap() {
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