package com.pedalgenie.pedalgenieback.domain.product.service;

import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortBy {

    RECENT(1L, "최신순"),
    NAME_ASC(2L, "이름순"),
    LIKE_DESC(3L, "좋아요순"),
    ;

    private final Long id;
    private final String name;

    public static SortBy from(Long id){
        for (SortBy sortBy : SortBy.values()){
            if (sortBy.id.equals(id)){
                return sortBy;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_SORT_BY);
    }

}
