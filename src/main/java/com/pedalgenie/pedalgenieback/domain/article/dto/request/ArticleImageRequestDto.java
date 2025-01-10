package com.pedalgenie.pedalgenieback.domain.article.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ArticleImageRequestDto {
    private String title;
    private String hashTag;
    private List<Long> productIds;
    private MultipartFile thumbnail;
    private MultipartFile body;
}
