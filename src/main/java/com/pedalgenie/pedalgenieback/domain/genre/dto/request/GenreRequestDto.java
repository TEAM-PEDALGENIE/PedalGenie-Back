package com.pedalgenie.pedalgenieback.domain.genre.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenreRequestDto {
    private String genreName;
    private List<Long> productsIdList;
}
