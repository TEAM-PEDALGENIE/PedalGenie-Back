package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopHoursDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopCreateResponse{
        private Long id;
        private String name;
        private String address;
        private String contactNumber;
        private String imageUrl;
        private String detailAddress;
        private String description;
        private Integer demoQuantityPerDay;
        private Integer instrumentCount;
        private List<ShopHoursDto> shopHours;

    // Shop 객체를 기반으로 ShopCreateResponse 생성
    public static ShopCreateResponse from(Shop shop) {
        return ShopCreateResponse.builder()
                .id(shop.getId())
                .name(shop.getShopname())
                .address(shop.getAddress())
                .contactNumber(shop.getContactNumber())
                .imageUrl(shop.getImageUrl())
                .detailAddress(shop.getDetailAddress())
                .description(shop.getDescription())
                .demoQuantityPerDay(shop.getDemoQuantityPerDay())
                .instrumentCount(shop.getInstrumentCount())
                .shopHours(shop.getShopHours().stream()
                        .map(ShopHoursDto::from) // ShopHours -> ShopHoursDto 변환
                        .toList())
                .build();
    }
}
