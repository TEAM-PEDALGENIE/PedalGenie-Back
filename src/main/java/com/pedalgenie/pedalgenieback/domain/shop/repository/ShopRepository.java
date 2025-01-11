package com.pedalgenie.pedalgenieback.domain.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>{

    Optional<Shop> findByShopname(final String shopname);

    @Query("SELECT s FROM Shop s WHERE s.shopname LIKE %:shopname%")
    List<Shop> findByShopnameContaining(@Param("shopname") String shopname);}
