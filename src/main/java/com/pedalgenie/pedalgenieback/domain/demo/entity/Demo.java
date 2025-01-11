package com.pedalgenie.pedalgenieback.domain.demo.entity;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Demo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long demoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @NotNull
    private LocalDateTime demoDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DemoStatus demoStatus;

    @Builder
    public Demo(Long demoId, Product product, Member member, Shop shop, LocalDateTime demoDate, DemoStatus demoStatus) {
        this.demoId = demoId;
        this.product = product;
        this.member = member;
        this.shop = shop;
        this.demoDate = demoDate;
        this.demoStatus = demoStatus;
    }

    public void updateDemoStatus(DemoStatus newStatus) {
        this.demoStatus = newStatus;
    }
}
