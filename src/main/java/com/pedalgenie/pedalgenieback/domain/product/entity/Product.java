package com.pedalgenie.pedalgenieback.domain.product.entity;

import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;


@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
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

//    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, REMOVE}, orphanRemoval = true)
//    private List<ProductImage> productImages = new ArrayList<>();

    @Builder
    public Product(Long id, String name, Double rentPricePerDay,
                   Integer rentQuantityPerDay, Double price, Shop shop, SubCategory subCategory,
                   Boolean isRentable, Boolean isPurchasable, Boolean isDemoable, String thumbnailImageUrl,
                   String descriptionUrl,
                   List<ProductImage> productImages){
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
//        this.productImages=productImages;
    }
}
