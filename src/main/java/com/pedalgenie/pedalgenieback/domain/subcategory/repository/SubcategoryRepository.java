package com.pedalgenie.pedalgenieback.domain.subcategory.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubcategoryRepository extends JpaRepository<SubCategory,Long> {

    List<SubCategory> findByCategory(Category category);

}
