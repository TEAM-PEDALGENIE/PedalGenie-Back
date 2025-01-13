package com.pedalgenie.pedalgenieback.domain.genre.entity;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GenreProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long genreProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public GenreProduct(Long genreProductId, Genre genre, Product product){
        this.genreProductId = genreProductId;
        this.genre = genre;
        this.product = product;
    }
}
