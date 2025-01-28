package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;

import java.math.BigDecimal;
import java.util.List;

// 상품 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record GetProductResponse(
        String name,
        Long shopId,
        String shopName,
        BigDecimal price,
        BigDecimal rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String descriptionUrl,
        List<ProductImageDto> productImage,
        Boolean isLiked,
        List<ShopHours> shopHours,
        String contactNumber,
        String address

) {
    public static GetProductResponse of(Product product, List<ProductImageDto> productImage, Boolean isLiked){
        return new GetProductResponse(
                product.getName(),
                product.getShop().getId(),
                product.getShop().getShopname(),
                product.getPrice(),
                product.getRentPricePerDay(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                product.getDescriptionUrl(),
                productImage,
                isLiked != null ? isLiked : null,
                product.getShop().getShopHours(),
                product.getShop().getContactNumber(),
                product.getShop().getAddress()
        );
    }
}
