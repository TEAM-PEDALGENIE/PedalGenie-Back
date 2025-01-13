package com.pedalgenie.pedalgenieback.domain.genre.repository;

import com.pedalgenie.pedalgenieback.domain.genre.entity.Genre;
import com.pedalgenie.pedalgenieback.domain.genre.entity.GenreProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenreProductRepository extends JpaRepository <GenreProduct, Long> {
    List<GenreProduct> findByGenre(Genre genre);
}
