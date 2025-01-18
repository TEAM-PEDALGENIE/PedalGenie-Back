package com.pedalgenie.pedalgenieback.domain.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;

import java.util.List;
import java.util.stream.Collectors;


// 매장 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetShopResponse (
        Long shopId,
        String shopname,
        String description,
        List<String> shopHours,
        String contactNumber,
        String address,
        Integer instrumentCount, // 보유 상품 개수 추가
        String shopImageUrl,
        Boolean isLiked,
        List<ProductResponse> products
) {
    public static GetShopResponse from(
            Shop shop,
            Boolean isLiked,
            List<ProductResponse> products,
            Integer instrumentCount){

            List<String> shopHours = shop.getShopHours()
                .stream()
                .map(GetShopResponse::formatShopHours)
                .collect(Collectors.toList());
        return new GetShopResponse(
                shop.getId(),
                shop.getShopname(),
                shop.getDescription(),
                shopHours,
                shop.getContactNumber(),
                shop.getAddress(),
                instrumentCount,
                shop.getImageUrl(),
                isLiked != null ? isLiked : null,
                products
        );
    }

    // ShopHours를 String으로 변환하는 메서드
    private static String formatShopHours(ShopHours shopHours) {
        String dayType = switch (shopHours.getDayType()) {
            case WEEKDAY -> "평일";
            case WEEKEND -> "주말";
            case HOLIDAY -> "공휴일";
            default -> shopHours.getDayType().name();
        };

        String timeRange = shopHours.getOpenTime() + "-" + shopHours.getCloseTime();
        String breakTime = (shopHours.getBreakStartTime() != null && shopHours.getBreakEndTime() != null)
                ? "\n" + shopHours.getBreakStartTime() + "-" + shopHours.getBreakEndTime()
                : "";

        return dayType + ": " + timeRange + breakTime;
    }
}
