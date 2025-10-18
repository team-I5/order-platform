package com.spartaclub.orderplatform.domain.category.application.service;

import com.spartaclub.orderplatform.domain.category.application.mapper.CategoryMapper;
import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.exception.CategoryErrorCode;
import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
//        CategoryType categoryType = CategoryType.getInstance(dto.getName());
//        Category category = categoryMapper.toCategoryEntity(dto, categoryType);
//        String type = treatName(dto.getName());
        // 2. requestDto → entity 전환
        Category category = Category.of(dto.getName());
        // 3. DB 저장 후 entity → responseDto 전환
        return categoryMapper.toCategoryResponseDto(categoryRepository.save(category));

    }

    // 카테고리 상세 조회
    @Transactional(readOnly = true)
    public CategoryResponseDto searchCategory(UUID categoryId) {
        return categoryMapper.toCategoryResponseDto(
            findCategory(categoryId));
    }

    // 카테고리 목록 조회
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> searchCategoryList(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toCategoryResponseDto);
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponseDto updateCategory(User user, UUID categoryId,
        CategoryRequestDto dto) {
        // 2. categoryId로 해당 카테코리 DB존재 확인
        Category category = findCategory(categoryId);
        category.updateCategory(dto.getName());
        category = categoryRepository.save(category);
        // 4. entity → responseDto 변환 뒤 반환
        return categoryMapper.toCategoryResponseDto(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(User user, UUID categoryId) {
        // 2. categoryId로 해당 카테코리 DB존재 확인
        Category category = findCategory(categoryId);
        // 3. 카테고리 도메인 삭제 메서드 호출
        category.deleteCategory(user.getUserId());
    }

//    public String treatName(String name) {
//        if (Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$", name)) {
//            if (name.equals("한식")) {
//                return "KOREANFOOD";
//            } else if (name.equals("중식")) {
//                return "CHINESEFOOD";
//            } else if (name.equals("분식")) {
//                return "SNACKFOOD";
//            } else if (name.equals("치킨")) {
//                return "CHICKEN";
//            } else if (name.equals("피자")) {
//                return "PIZZA";
//            } else if (name.equals("양식")) {
//                return "WESTERNFOOD";
//            } else if (name.equals("일식")) {
//                return "JAPANESEFOOD";
//            }
//        }
//        return name;
//    }


    // 존재하는 카테고리인지 확인
    @Transactional(readOnly = true)
    public Category findCategory(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(
                () -> new BusinessException(CategoryErrorCode.NOT_EXIST));
    }
}
