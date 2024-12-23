package com.pedalgenie.pedalgenieback.domain.product.service;

import com.pedalgenie.pedalgenieback.domain.product.dto.request.ProductCreateRequestDto;
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


}
