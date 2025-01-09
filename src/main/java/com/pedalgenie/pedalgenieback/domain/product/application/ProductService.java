package com.pedalgenie.pedalgenieback.domain.product.application;

import com.pedalgenie.pedalgenieback.domain.product.dto.request.ProductCreateRequest;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.domain.subcategory.repository.SubcategoryRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final SubcategoryRepository subcategoryRepository;

    public Product createProduct(
            final ProductCreateRequest request,
            final String descriptionUrl){

        final Shop shop = getShop(request.shopName());
        final SubCategory subCategory = getSubCategory(request.subCategoryId());

        // Product 엔티티 생성
        Product product = Product.builder()
                .name(request.name())
                .rentPricePerDay(request.rentPricePerDay())
                .rentQuantityPerDay(request.rentQuantityPerDay())
                .price(request.price())
                .isRentable(request.isRentable())
                .isPurchasable(request.isPurchasable())
                .isDemoable(request.isDemoable())
                .descriptionUrl(descriptionUrl)
                .shop(shop)
                .subCategory(subCategory)
                .build();


        return productRepository.save(product); // 상품 설명 이미지 DB 저장

    }

    private Shop getShop(final String shopname){
        return shopRepository.findByShopname(shopname)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));
    }

    private SubCategory getSubCategory(final Long subCategoryId){
        return subcategoryRepository.findById(subCategoryId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SUBCATEGORY));
    }

}
