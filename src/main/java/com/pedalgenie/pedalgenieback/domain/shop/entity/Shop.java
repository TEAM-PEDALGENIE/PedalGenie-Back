package com.pedalgenie.pedalgenieback.domain.shop.entity;

import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "shop")
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String shopname;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private Integer demoQuantityPerDay;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer instrumentCount;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopHours> shopHours = new ArrayList<>();


    @Builder
    public Shop(Long id, String name, String address, String contactNumber, Integer demoQuantityPerDay,
                String imageUrl, String detailAddress, String description, Integer instrumentCount){
        this.id= id;
        this.shopname =name;
        this.address=address;
        this.contactNumber=contactNumber;
        this.demoQuantityPerDay=demoQuantityPerDay;
        this.imageUrl=imageUrl;
        this.detailAddress=detailAddress;
        this.description=description;
        this.instrumentCount=instrumentCount;
    }

    public void addShopHours(List<ShopHours> hoursList) {
        this.shopHours.addAll(hoursList);
    }
}
