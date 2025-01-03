package com.pedalgenie.pedalgenieback.domain.like.repository;

import com.pedalgenie.pedalgenieback.domain.like.entity.ProductLike;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    // productId를 기준으로 좋아요가 몇 개인지 카운트하는 메서드
    @Query("SELECT COUNT(pl) FROM ProductLike pl WHERE pl.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    // 상품, 멤버 기준으로 좋아요가 존재하는지 반환하는 메서드
    boolean existsByProductAndMember(Product product, Member member);
}

