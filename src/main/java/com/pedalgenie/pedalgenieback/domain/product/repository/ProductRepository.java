package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 특정 하위 카테고리에 속한 상품 조회
    @Query("select distinct p from Product p join fetch p.subCategory " +
            "where p.subCategory.id = :id")
    List<Product> findBySubCategoryId(@Param("id")Long id);

    // 특정 카테고리에 속한 상품 조회
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.subCategory sc " +
            "JOIN FETCH sc.category c " +
            "WHERE c.id = :id")
    List<Product> findAllBySubCategoryCategoryId(@Param("id")Long id);


}
