package com.pedalgenie.pedalgenieback.domain.productImage.application;

import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.productImage.repository.ProductImageRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageQueryService {

    private final ProductImageRepository productImageRepository;

    // 첫 번째 단일 이미지
    public ProductImageDto getFirstProductImage(Long productId){
        ProductImage firstImage = productImageRepository
                .findFirstByProductId(productId, Sort.by(Sort.Direction.ASC, "id")) // 내림차순 썸네일
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_PRODUCT_IMAGE));

        return ProductImageDto.fromEntity(firstImage);
    }

    // 이미지 목록
    public List<ProductImageDto> getProductImages(Long productId){
        List<ProductImage> images = productImageRepository.findByProductId(productId);

        return images.stream()
                .map(productImage -> new ProductImageDto(productImage.getImageUrl()))
                .toList();
    }

}
