package com.spartaclub.orderplatform.user.domain.entity;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * 주소 엔티티 클래스 사용자의 배송지 주소 정보 관리
 *
 * @author 전우선
 * @date 2025-10-09(목)
 */
@Entity
@Table(name = "p_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "addressId")
    private UUID addressId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phoneNumber", nullable = false, length = 13)
    private String phoneNumber;

    @Column(name = "postCode", nullable = false, length = 10)
    private String postCode;

    @Column(name = "roadNameAddress", nullable = false, length = 255)
    private String roadNameAddress;

    @Column(name = "detailedAddress", nullable = false, length = 255)
    private String detailedAddress;

    @Column(name = "addressName", nullable = false, length = 50)
    private String addressName;

    @Column(name = "defaultAddress", nullable = false)
    @Builder.Default
    private Boolean defaultAddress = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}