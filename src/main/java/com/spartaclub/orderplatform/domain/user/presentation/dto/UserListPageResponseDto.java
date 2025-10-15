package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 회원 전체 조회 페이지 응답 DTO
 * 페이징 정보와 통계 정보를 포함한 완전한 응답 클래스
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListPageResponseDto {

    // 회원 목록
    private List<UserListResponseDto> content;

    // 페이징 정보
    private PageableInfo pageable;

    // 통계 정보
    private SummaryInfo summary;

    /**
     * 페이징 정보 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageableInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }

    /**
     * 통계 정보 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryInfo {
        private long totalUsers;
        private long activeUsers;
        private long deletedUsers;
        private Map<String, Long> roleDistribution;
    }
}