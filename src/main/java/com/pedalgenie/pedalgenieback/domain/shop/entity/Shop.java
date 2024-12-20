package com.pedalgenie.pedalgenieback.domain.shop.entity;

import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String contactNumber;

    @NotNull
    private int demoQuantityPerDay;

    // businessHours

    @Builder
    public Shop(Long shopId, String name, String address, String contactNumber, int demoQuantityPerDay){
        this.shopId=shopId;
        this.name=name;
        this.address=address;
        this.contactNumber=contactNumber;
        this.demoQuantityPerDay=demoQuantityPerDay;
    }
}
