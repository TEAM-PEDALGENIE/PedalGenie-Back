package com.pedalgenie.pedalgenieback.domain.article.repository;

import com.pedalgenie.pedalgenieback.domain.article.entity.Article;
import com.pedalgenie.pedalgenieback.domain.article.entity.ArticleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleProductRepository extends JpaRepository<ArticleProduct, Long> {
    // 특정 article에 연결된 ArticleProduct 삭제
    @Modifying
    @Query("DELETE FROM ArticleProduct ap WHERE ap.article.articleId = :articleId")
    void deleteByArticleId(@Param("articleId") Long articleId);

    // 특정 product에 연결된 ArticleProduct 검색
    @Query("SELECT ap FROM ArticleProduct ap WHERE ap.product.id = :productId")
    List<ArticleProduct> findByProductId(@Param("productId") Long productId);

    // 특정 Article에 연결된 ArticleProduct 검색
    List<ArticleProduct> findByArticle(Article article);
}
