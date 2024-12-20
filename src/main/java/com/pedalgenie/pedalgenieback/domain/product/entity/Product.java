package com.pedalgenie.pedalgenieback.domain.product.entity;

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
    private String description;

    @NotNull
    private double rentPricePerDay;

    @NotNull
    private int rentQuantityPerDay;

    @ManyToOne
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "subcategory_id") // 외래키 컬럼명. 변경할까?
    private SubCategory subCategory;

    @Builder
    public Product(Long id, String name, String description, double rentPricePerDay, int
            rentQuantityPerDay, Shop shop, SubCategory subCategory){
        this.id=id;
        this.name=name;
        this.description=description;
        this.rentPricePerDay=rentPricePerDay;
        this.rentQuantityPerDay=rentQuantityPerDay;
        this.shop=shop;
        this.subCategory=subCategory;
    }
}
