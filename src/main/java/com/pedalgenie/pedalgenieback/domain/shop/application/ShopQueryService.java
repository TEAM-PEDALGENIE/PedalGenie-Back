package com.pedalgenie.pedalgenieback.domain.shop.application;

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
    private final ProductQueryService productQueryService;
    private final ProductImageQueryService productImageQueryService;

    // 매장 목록 조회
    public GetShopsResponses readShops(){
        List<Shop> shops = shopRepository.findAll();

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
        return GetShopsResponses.from(shops, shopProductMap);
    }

    // 매장 상세 조회
    public GetShopResponse readShop(Long id){
        Shop shop = shopRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXISTS_SHOP_ID));

        List<ProductResponse> products = productQueryService.getProductsByShop(id);

        return GetShopResponse.from(shop, products);

    }

    // 매장 상품 필터 옵션 조회

}
