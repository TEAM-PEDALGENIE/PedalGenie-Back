package com.pedalgenie.pedalgenieback.domain.shop.entity;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "shop")
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

    @Column(nullable = false)
    private String imageUrl;


    private String detailAddress;


    private String description;


    private Integer instrumentCount;


    @Builder
    public Shop(Long id, String name, String address, String contactNumber, Integer demoQuantityPerDay, String businessHours,
                String imageUrl, String detailAddress, String description, Integer instrumentCount){
        this.id= id;
        this.shopname =name;
        this.address=address;
        this.contactNumber=contactNumber;
        this.demoQuantityPerDay=demoQuantityPerDay;
        this.businessHours=businessHours;
        this.imageUrl=imageUrl;
        this.detailAddress=detailAddress;
        this.description=description;
        this.instrumentCount=instrumentCount;
    }
}
