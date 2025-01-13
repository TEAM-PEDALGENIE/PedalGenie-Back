package com.pedalgenie.pedalgenieback.domain.genre.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenreResponseDto {
    private Long genreId;
    private String genreName;
    private List<Long> productsIdList;
}
