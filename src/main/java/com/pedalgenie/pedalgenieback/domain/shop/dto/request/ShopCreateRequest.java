package com.pedalgenie.pedalgenieback.domain.shop.dto.request;

import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopCreateRequest {

    private String shopname;
    private String address;
    private String contactNumber;
    private Integer demoQuantityPerDay;
    private String detailAddress;
    private String description;
    private Integer instrumentCount;
    private List<ShopHoursDto> shopHours; //운영시간 추가


    public Shop toEntity(String url) {
        Shop shop = Shop.builder()
                .name(shopname)
                .address(address)
                .contactNumber(contactNumber)
                .demoQuantityPerDay(demoQuantityPerDay)
                .imageUrl(url)
                .detailAddress(detailAddress)
                .description(description)
                .instrumentCount(instrumentCount)
                .build();

        List<ShopHours> shopHoursList = shopHours.stream()
                .map(dto -> ShopHoursDto.toEntity(dto, shop))
                .toList();

        shop.addShopHours(shopHoursList);

        return shop;

    }

    public List<ShopHours> toShopHoursEntities(Shop shop) {
        return shopHours.stream()
                .map(dto -> ShopHoursDto.toEntity(dto, shop))
                .toList();
    }
}
