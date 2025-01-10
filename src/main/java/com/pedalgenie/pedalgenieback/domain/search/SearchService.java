package com.pedalgenie.pedalgenieback.domain.search;

import com.pedalgenie.pedalgenieback.domain.like.application.LikeService;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.productImage.repository.ProductImageRepository;
import com.pedalgenie.pedalgenieback.domain.shop.application.ShopDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final ProductImageQueryService productImageQueryService;
    private final LikeService likeService;

    @Cacheable(value = "search", key = "#keyword")
    public SearchResponse search(String keyword, Long memberId){

        keyword = keyword.trim();

        List<Product> products = productRepository.findByNameContaining(keyword);
        List<Shop> shops = shopRepository.findByShopnameContaining(keyword);

        if (products.isEmpty() && shops.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXISTS_PRODUCT_OR_SHOP);
        }

        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    ProductImageDto productImageDto =  getProductImage(product);
                    Boolean isLiked = (memberId != null)
                            ? likeService.isProductLiked(product.getId(), memberId) : null;

                    return ProductResponse.from(product,productImageDto,isLiked);

                })
                .toList();

        List<ShopDto> shopDtos = shops.stream()
                .map(shop -> {
                    Boolean shopIsLiked = (memberId !=null)
                            ? likeService.isShopLiked(shop.getId(),memberId)
                            : null;
                    return ShopDto.from(shop, shopIsLiked);
                })
                .toList();

        return new SearchResponse(productResponses, shopDtos);
    }


    private ProductImageDto getProductImage(Product product) {
        return productImageQueryService.getFirstProductImage(product.getId());
    }


}
