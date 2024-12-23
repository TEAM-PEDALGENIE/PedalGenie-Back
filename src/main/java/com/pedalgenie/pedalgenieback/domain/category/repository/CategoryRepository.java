package com.pedalgenie.pedalgenieback.domain.category.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
}
