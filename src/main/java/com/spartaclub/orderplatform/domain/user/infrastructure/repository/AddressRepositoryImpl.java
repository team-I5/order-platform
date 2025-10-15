package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AddressRepository 도메인 인터페이스의 구현체
 * JPA 기술을 사용하여 Address 도메인 데이터 접근 구현
 * Infrastructure 계층에서 도메인 계층의 추상화를 구체화
 *
 * @author 전우선
 * @date 2025-10-15
 */
@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {

    private final AddressJpaRepository addressJpaRepository;

    @Override
    public Address save(Address address) {
        return addressJpaRepository.save(address);
    }

    @Override
    public Optional<Address> findById(java.util.UUID addressId) {
        return addressJpaRepository.findById(addressId);
    }

    @Override
    public void delete(Address address) {
        addressJpaRepository.delete(address);
    }

    @Override
    public List<Address> findByUser(User user) {
        return addressJpaRepository.findByUser(user);
    }

    @Override
    public List<Address> findByUserId(Long userId) {
        return addressJpaRepository.findByUserUserId(userId);
    }

    @Override
    public Optional<Address> findDefaultByUser(User user) {
        return addressJpaRepository.findByUserAndIsDefaultTrue(user);
    }

    @Override
    public Optional<Address> findDefaultByUserId(Long userId) {
        return addressJpaRepository.findByUserUserIdAndIsDefaultTrue(userId);
    }

    @Override
    public AddressPage findByUser(User user, AddressPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<Address> page = addressJpaRepository.findByUser(user, springPageRequest);
        return convertToAddressPage(page);
    }

    @Override
    public AddressPage findByUserId(Long userId, AddressPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<Address> page = addressJpaRepository.findByUserUserId(userId, springPageRequest);
        return convertToAddressPage(page);
    }

    private PageRequest createSpringPageRequest(AddressPageRequest pageRequest) {
        Sort sort = pageRequest.ascending() 
            ? Sort.by(pageRequest.sortBy()).ascending()
            : Sort.by(pageRequest.sortBy()).descending();
        
        return PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
    }

    @Override
    public boolean existsByUserAndAddressName(User user, String addressName) {
        return addressJpaRepository.existsByUserAndAddressName(user, addressName);
    }

    @Override
    public long countByUser(User user) {
        return addressJpaRepository.countByUser(user);
    }

    @Override
    public List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user) {
        return addressJpaRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    @Override
    public List<Address> findActiveByUserOrderByIsDefaultDescCreatedAtDesc(User user) {
        // 소프트 삭제가 적용된다면 deletedAt이 null인 것만 조회
        return addressJpaRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    @Override
    public List<Address> findActiveByUserExcludingId(User user, java.util.UUID addressId) {
        return addressJpaRepository.findByUserAndAddressIdNotOrderByCreatedAtDesc(user, addressId);
    }

    private AddressPage convertToAddressPage(Page<Address> page) {
        return new AddressPage(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}