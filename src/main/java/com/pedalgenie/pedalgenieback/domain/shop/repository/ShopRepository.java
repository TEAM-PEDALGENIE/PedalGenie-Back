package com.pedalgenie.pedalgenieback.domain.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>{
}
