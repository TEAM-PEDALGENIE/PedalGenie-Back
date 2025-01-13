package com.pedalgenie.pedalgenieback.domain.article.repository;

import com.pedalgenie.pedalgenieback.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
