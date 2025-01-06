package com.pedalgenie.pedalgenieback.domain.product.application;

import com.pedalgenie.pedalgenieback.domain.product.dto.request.ProductCreateRequestDto;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductDescriptionUrlResponse;
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

    public void createProduct(final ProductCreateRequestDto request, final String shopname, final Long subCategoryId){
        final Shop shop = shopRepository.findByShopname(shopname)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));

        final SubCategory subCategory = getSubCategory(subCategoryId);

        final Product product = request.toEntity(shop, subCategory);
        productRepository.save(product);

    }

    private SubCategory getSubCategory(final Long subCategoryId){
        return subcategoryRepository.findById(subCategoryId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SUBCATEGORY));
    }

    public void updateProduct(Long id){

    }

    // 상품 설명 이미지 업로드
    public ProductDescriptionUrlResponse saveProductDescription(Long productId, String url){

        Product product= productRepository.findById(productId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        Product updatedProduct = product.toBuilder()
                .descriptionUrl(url)
                .build();

        productRepository.save(updatedProduct);

        return ProductDescriptionUrlResponse.from(url);

    }



}
