package com.pedalgenie.pedalgenieback.domain.like.repository;

import com.pedalgenie.pedalgenieback.domain.like.entity.ProductLike;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    // productId를 기준으로 좋아요가 몇 개인지 카운트하는 메서드
    @Query("SELECT COUNT(pl) FROM ProductLike pl WHERE pl.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    // 상품, 멤버 기준으로 좋아요가 존재하는지 반환하는 메서드
    boolean existsByProductAndMember(Product product, Member member);
    Optional<ProductLike> findByProductAndMember(Product product, Member member);

    // memberId 기준으로 좋아요한 productId 목록 조회메서드
    @Query("SELECT l.product.id FROM ProductLike l WHERE l.member.memberId = :memberId AND l.product.id IN :productIds")
    List<Long> findLikedProductIdsByMember(@Param("memberId") Long memberId, @Param("productIds") List<Long> productIds);

    // memberId 기준으로 좋아요한 product 목록 조회 메서드
    @Query("SELECT l.product FROM ProductLike l WHERE l.member.memberId = :memberId")
    List<Product> findLikedProductsByMember(@Param("memberId") Long memberId);

    // memberId 기준으로 좋아요 삭제하는 메서드
    @Modifying
    @Query("DELETE FROM ProductLike pl WHERE pl.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}

