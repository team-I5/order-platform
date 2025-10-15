package com.spartaclub.orderplatform.domain.category.application.service;

import com.spartaclub.orderplatform.domain.category.application.mapper.CategoryMapper;
import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.domain.model.CategoryType;
import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategorySearchRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/*
 * Cateogory 서비스 클래스
 * 카테고리 관련 비즈니스 로직 처리
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    // 필드 선언
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
//    private final StoreRepository storeRepository;

    // 카테고리 생성
    @Transactional
    public CategoryResponseDto createCategory(User user, CategoryRequestDto dto) {
        // 1. 카테고리 관리자 등급 확인
//        if (!user.getRole().equals("MASTER")) {
//            throw new IllegalArgumentException("유효한 카테고리 등록자가 아닙니다.");
//        }
        /*
         * ▶고민해본 요소들
         * 가게 이름으로 해당 store행 도출 후 storeId 뽑아옴
         * 음식점ID로 카테고리 요소를 가져와야 하는 상황이 생겼을 때 적용 고민
        Store store = storeRepository.findByStore_StoreNameAndDeletedAtIsNull(dto.getStoreName());
        store.getStoreId();
         * 객체 인스턴스를 통해 EnumType에 접근해 값 가져오기
         * 이 카테고리 유형으로 해당되는 모든 음식점ID 뽑아와야 하는 경우 적용 고민
        CategoryType type = CategoryType.getInstance(dto.getName());
        */
        // 2. requestDto → entity 전환
        CategoryType categoryType = CategoryType.getInstance(dto.getName());
        Category category = categoryMapper.toCategoryEntity(dto, categoryType);
        // 3. DB 저장 후 entity → responseDto 전환
        return categoryMapper.toCategoryResponseDto(categoryRepository.save(category));

    }

    // 카테고리 조회
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> searchCategory(CategorySearchRequestDto dto) {
        // Pageable로 페이지 단위 처리 설정
        Pageable pageable = PageRequest.of(
            dto.getPage(), dto.getSize(),
            Sort.by(dto.getDirection(), "createdAt")
        );
        // 카테고리 타입이 존재하면 카테고리 타입으로 조회
        if (dto.getCategoryType() != null) {
            return categoryRepository.findByTypeAndDeletedAtIsNull(
                    CategoryType.valueOf(dto.getCategoryType()), pageable)
                .map(categoryMapper::toCategoryResponseDto);
            // 카테고리 타입이 존재하지 않으면 전체 타입으로 조회
        } else {
            return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryResponseDto);
        }
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponseDto updateCategory(User user, UUID categoryId,
        CategoryRequestDto dto) {
        // 1. 카테고리 관리자 등급 확인
//        if (!user.getRole().equals("MASTER")) {
//            throw new IllegalArgumentException("유효한 카테고리 관리자가 아닙니다.");
//        }
        // 2. categoryId로 해당 카테코리 DB존재 확인
        Category category = findCategory(categoryId);

        if (category == null) {
            // 3. 카테고리 엔티티 update 함수에서 변경된 값 반영
            CategoryType tmp = CategoryType.valueOf(dto.getName());
            category.updateReview(tmp);
        }
        // 4. entity → responseDto 변환 뒤 반환
        return categoryMapper.toCategoryResponseDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(User user, UUID categoryId) {
        // 1. 카테고리 관리자 등급 확인
//        if (!user.getRole().equals("MASTER")) {
//            throw new IllegalArgumentException("유효한 카테고리 관리자가 아닙니다.");
//        }
        // 2. categoryId로 해당 카테코리 DB존재 확인
        Category category = findCategory(categoryId);
        // 3. 카테고리 도메인 삭제 메서드 호출
        category.deleteCategory(user.getUserId());
    }

    // 존재하는 카테고리인지 확인
    @Transactional(readOnly = true)
    public Category findCategory(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));
    }
}
