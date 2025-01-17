package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByShop(Shop shop);

    List<Product> findByShopId(Long shopId);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);

    List<Product> findAllByIsRentableTrue(); // isRentable이 true인 상품만 조회

    // 상점의 보유 상품 개수 가져오기
    @Query("SELECT COUNT(p) FROM Product p WHERE p.shop.id = :shopId")
    Integer countProductsByShopId(@Param("shopId") Long shopId);
}
