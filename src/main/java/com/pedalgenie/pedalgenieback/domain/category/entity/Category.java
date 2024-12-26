package com.pedalgenie.pedalgenieback.domain.category.entity;

import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotNull
    private String name;

    @Builder
    public Category(Long id, String name){
        this.id=id;
        this.name=name;
    }
}
