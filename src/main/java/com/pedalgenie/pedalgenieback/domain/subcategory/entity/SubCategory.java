package com.pedalgenie.pedalgenieback.domain.subcategory.entity;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class SubCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subCategory_id")
    private Long id;

    @Column(nullable = false, unique = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    public SubCategory(Long id, String name, Category category){
        this.id=id;
        this.name=name;
        this.category=category;

    }
}
