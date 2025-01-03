package com.pedalgenie.pedalgenieback.domain.product.entity;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Getter
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Double rentPricePerDay;

    @NotNull
    private Integer rentQuantityPerDay;

    @NotNull
    private Double price;

    private Boolean isRentable;

    private Boolean isPurchasable;

    private Boolean isDemoable;

    private String thumbnailImageUrl; // 썸네일 이미지

    private String descriptionUrl; // 매장 설명 이미지

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategory subCategory;

//    @Enumerated(EnumType.STRING)
//    private Category category;

    @Builder
    public Product(Long id, String name, Double rentPricePerDay,
                   Integer rentQuantityPerDay, Double price, Shop shop, SubCategory subCategory,
                   Boolean isRentable, Boolean isPurchasable, Boolean isDemoable, String thumbnailImageUrl,
                   String descriptionUrl){
        this.id=id;
        this.name=name;
        this.rentPricePerDay=rentPricePerDay;
        this.rentQuantityPerDay=rentQuantityPerDay;
        this.price=price;
        this.shop=shop;
        this.subCategory=subCategory;
        this.isRentable=isRentable;
        this.isPurchasable=isPurchasable;
        this.isDemoable=isDemoable;
        this.thumbnailImageUrl=thumbnailImageUrl;
        this.descriptionUrl=descriptionUrl;
    }
}
