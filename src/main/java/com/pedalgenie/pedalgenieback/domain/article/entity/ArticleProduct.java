package com.pedalgenie.pedalgenieback.domain.article.entity;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public ArticleProduct(Long articleProductId, Article article, Product product) {
        this.articleProductId = articleProductId;
        this.article = article;
        this.product = product;
    }
}
