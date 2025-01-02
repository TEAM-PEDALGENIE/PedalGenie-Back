package com.pedalgenie.pedalgenieback.domain.shop.entity;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @NotNull
    private String shopname;

    @NotNull
    private String address;

    @NotNull
    private String contactNumber;

    @NotNull
    private Integer demoQuantityPerDay;

    @NotNull
    private String businessHours;

    private String imageUrl;

    // 매장 당 보유 악기 개수


//    private List<Product> products; // 단방향 연관관계이므로 shop에서는 제거

    @Builder
    public Shop(Long id, String name, String address, String contactNumber, Integer demoQuantityPerDay,
                String businessHours, String imageUrl){
        this.id= id;
        this.shopname =name;
        this.address=address;
        this.contactNumber=contactNumber;
        this.demoQuantityPerDay=demoQuantityPerDay;
        this.businessHours=businessHours;
        this.imageUrl=imageUrl;
    }
}
