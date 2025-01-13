package com.pedalgenie.pedalgenieback.domain.productImage.application;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import com.pedalgenie.pedalgenieback.domain.productImage.repository.ProductImageRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductImage saveProductImage(Long productId, String url){
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        ProductImage productImage= ProductImage.builder()
                .imageUrl(url)
                .product(product)
                .build();

        // ProductImage 객체를 DB에 저장
        return productImageRepository.save(productImage);
    }
}
