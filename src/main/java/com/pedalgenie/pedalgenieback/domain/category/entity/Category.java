package com.pedalgenie.pedalgenieback.domain.category.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    GUITAR(1L, "기타"),
    BASE(2L, "베이스"),
    KEYBOARD(3L, "키보드"),
    DRUM(4L, "드럼"),
    ORCHESTRA(5L, "관현악")
    ;
    private final Long id;
    private final String name;


}
