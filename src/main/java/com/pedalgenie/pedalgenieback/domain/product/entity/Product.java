package com.pedalgenie.pedalgenieback.domain.product.entity;

import com.pedalgenie.pedalgenieback.domain.like.entity.ProductLike;
import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;



@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double rentPricePerDay;

    @Column(nullable = false)
    private Integer rentQuantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean isRentable;

    @Column(nullable = false)
    private Boolean isPurchasable;

    @Column(nullable = false)
    private Boolean isDemoable;

    private String descriptionUrl; // 상품 설명 이미지

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, REMOVE}, orphanRemoval = true)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, REMOVE}, orphanRemoval = true)
    private List<ProductLike> productLikes;

    @Builder
    public Product(String name, Double rentPricePerDay,
                   Integer rentQuantity, Double price, Shop shop, SubCategory subCategory,
                   Boolean isRentable, Boolean isPurchasable, Boolean isDemoable, String descriptionUrl
                   ){

        this.name=name;
        this.rentPricePerDay=rentPricePerDay;
        this.rentQuantity =rentQuantity;
        this.price=price;
        this.isRentable=isRentable;
        this.isPurchasable=isPurchasable;
        this.isDemoable=isDemoable;
        this.shop=shop;
        this.subCategory=subCategory;
        this.descriptionUrl=descriptionUrl;
    }

    public void decreaseStock(){
        if(rentQuantity <= 0){
            throw new CustomException(ErrorCode.INVALID_RENT_QUANTITY);
        }
        rentQuantity--;
    }
    public void increaseStock(Integer rentQuantity){
        this.rentQuantity += rentQuantity;
    }

}
