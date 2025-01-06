package com.pedalgenie.pedalgenieback.domain.like.repository;

import com.pedalgenie.pedalgenieback.domain.like.entity.ShopLike;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ShopLikeRepository extends JpaRepository<ShopLike, Long> {
    // shopId를 기준으로 좋아요가 몇 개인지 카운트하는 메서드
    @Query("SELECT COUNT(sl) FROM ShopLike sl WHERE sl.shop.id = :shopId")
    Long countByShopId(@Param("shopId") Long shopId);
    // 가게, 멤버를 기준으로 좋아요가 존재하는지 반환하는 메서드
    boolean existsByShopAndMember(Shop shop, Member member);
    Optional<ShopLike> findByShopAndMember(Shop shop, Member member);

    // 회원이 좋아요 누른 상점 리스트 반환하는 메서드
    @Query("SELECT sl.shop FROM ShopLike sl WHERE sl.member = :member")
    List<Shop> findLikedShopsByMember(@Param("member") Member member);

    // memberId를 기준으로 상점 좋아요 Id 리스트 반환하는 메서드
    @Query("SELECT sl.shop.id FROM ShopLike sl WHERE sl.member.memberId = :memberId")
    List<Long> findLikedShopIdsByMemberId(@Param("memberId") Long memberId);
}

