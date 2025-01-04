package com.pedalgenie.pedalgenieback.domain.productImage.repository;

import com.pedalgenie.pedalgenieback.domain.productImage.ProductImage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // 오름차순 첫 번째 이미지 반환
    Optional<ProductImage> findFirstByProductId(Long productId, Sort sort);

    // 모든 이미지 목록
    List<ProductImage> findByProductId(Long productId);

}
