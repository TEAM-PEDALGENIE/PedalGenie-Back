package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.pedalgenie.pedalgenieback.domain.like.application.LikeService;
import com.pedalgenie.pedalgenieback.domain.like.repository.ShopLikeRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.product.application.ProductQueryService;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopResponse;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopsResponses;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.ShopProductResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopQueryService {
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final LikeService likeService;
    private final ProductQueryService productQueryService;
    private final ProductImageQueryService productImageQueryService;

    // 매장 목록 조회
    public GetShopsResponses readShops(Long memberId){
        List<Shop> shops = shopRepository.findAll();

        List<Long> likedShopIds = (memberId != null) ?
                shopLikeRepository.findLikedShopIdsByMemberId(memberId) :
                List.of();


        Map<Shop, List<ShopProductResponse>> shopProductMap = shops.stream()
                .collect(Collectors.toMap(
                        shop -> shop,
                        shop -> productRepository.findByShop(shop).stream()
                                .map(product -> {
                                    ProductImageDto productImage = productImageQueryService.getFirstProductImage(product.getId());
                                    return ShopProductResponse.from(product,productImage);
                                })
                                .toList()
                ));
        return GetShopsResponses.from(shops, shopProductMap, likedShopIds);
    }

    // 매장 상세 조회
    public GetShopResponse readShop(Long shopId, Long memberId){
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXISTS_SHOP));

        // 좋아요 여부 확인
        Boolean isLiked = (memberId != null) &&
                likeService.isShopLiked(shopId, memberId) ? true: false;

        Integer instrumentCount = productRepository.countProductsByShopId(shopId);

        List<ProductResponse> products = productQueryService.getProductsByShop(shopId, memberId);

        return GetShopResponse.from(
                shop,
                memberId!=null ? isLiked: null, // 로그인한 유저만 필드 포함
                products,
                instrumentCount);

    }


}
