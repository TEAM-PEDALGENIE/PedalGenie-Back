package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;

import java.util.List;

// 상품 상세 조회 dto
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드 제외
public record GetProductResponse(
        String name,
        Long shopId,
        String shopName,
        Long price,
        Long rentPricePerDay,
        Boolean isRentable,
        Boolean isPurchasable,
        Boolean isDemoable,
        String descriptionUrl,
        List<ProductImageDto> productImage,
        Boolean isLiked,
        Boolean isShopLiked,
        List<ShopHours> shopHours,
        String contactNumber,
        String address,
        String shopImage

) {
    public static GetProductResponse of(Product product, List<ProductImageDto> productImage, Boolean isLiked, Boolean isShopLiked){

        return new GetProductResponse(
                product.getName(),
                product.getShop().getId(),
                product.getShop().getShopname(),
                product.getPrice().longValue(),
                product.getRentPricePerDay().longValue(),
                product.getIsRentable(),
                product.getIsPurchasable(),
                product.getIsDemoable(),
                product.getDescriptionUrl(),
                productImage,
                isLiked != null ? isLiked : null,
                isShopLiked !=null ? isShopLiked :null,
                product.getShop().getShopHours(),
                product.getShop().getContactNumber(),
                product.getShop().getAddress(),
                product.getShop().getImageUrl()
        );
    }
}
