package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.pedalgenie.pedalgenieback.domain.like.application.LikeService;
import com.pedalgenie.pedalgenieback.domain.like.repository.ProductLikeRepository;
import com.pedalgenie.pedalgenieback.domain.like.repository.ShopLikeRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
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
    private final ProductLikeRepository productLikeRepository;
    private final LikeService likeService;
    private final ProductQueryService productQueryService;
    private final ProductImageQueryService productImageQueryService;

    // 매장 목록 조회
    public GetShopsResponses readShops(Long memberId) {
        List<Shop> shops = shopRepository.findAll();

        boolean isLoggedIn = memberId != null;
        List<Long> likedShopIds = isLoggedIn ?
                shopLikeRepository.findLikedShopIdsByMemberId(memberId) :
                List.of();

        Map<Shop, List<ShopProductResponse>> shopProductMap = shops.stream()
                .collect(Collectors.toMap(
                        shop -> shop,
                        shop -> {
                            List<Product> products = productRepository.findByShop(shop);
                            // 좋아요한 상품 ID 리스트 조회
                            List<Long> likedProductIds = isLoggedIn ?
                                    productQueryService.getLikedProductIds(memberId, products.stream().map(Product::getId).toList()) :
                                    List.of();

                            return products.stream()
                                    .map(product -> {
                                        ProductImageDto productImage = productImageQueryService.getFirstProductImage(product.getId());
                                        Boolean isProductLiked = likedProductIds.contains(product.getId()); // 좋아요 여부 확인
                                        return ShopProductResponse.from(product, productImage, isProductLiked);
                                    })
                                    .toList();
                        }
                ));

        return GetShopsResponses.from(shops, shopProductMap, likedShopIds, isLoggedIn);
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
