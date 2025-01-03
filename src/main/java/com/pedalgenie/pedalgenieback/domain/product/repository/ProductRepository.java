package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    // 특정 하위 카테고리에 속한 상품 조회
//    @Query("select distinct p from Product p join fetch p.subCategory " +
//            "where p.subCategory.id = :id")
//    List<Product> findBySubCategoryId(@Param("id")Long id);

//    List<Product> findAllByCategory(Category category);

    List<Product> findByShop(Shop shop);

    List<Product> findByShopId(Long shopId);

//    List<Product> findByNameContaining(String name);

}
