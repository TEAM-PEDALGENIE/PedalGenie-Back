package com.pedalgenie.pedalgenieback.domain.like.repository;

import com.pedalgenie.pedalgenieback.domain.like.entity.ShopLike;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ShopLikeRepository extends JpaRepository<ShopLike, Long> {
    // shopId를 기준으로 좋아요가 몇 개인지 카운트하는 메서드
    @Query("SELECT COUNT(sl) FROM ShopLike sl WHERE sl.shop.id = :shopId")
    Long countByShopId(@Param("shopId") Long shopId);
    // 가게, 멤버를 기준으로 좋아요가 존재하는지 반환하는 메서드
    boolean existsByShopAndMember(Shop shop, Member member);
    Optional<ShopLike> findByShopAndMember(Shop shop, Member member);

}

