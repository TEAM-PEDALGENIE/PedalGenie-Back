package com.pedalgenie.pedalgenieback.domain.article.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleResponseDto {
    private Long articleId;
    private String title;
    private List<String> hashTag;
    private List<Long> productIds;
    private String thumbnailUrl;
    private String bodyUrl;
    private List<ArticleProductResponseDto> products;
}
