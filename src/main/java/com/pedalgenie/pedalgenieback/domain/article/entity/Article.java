package com.pedalgenie.pedalgenieback.domain.article.entity;

import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Article extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    private String title;

    private String hashTag;

    private String thumbnailUrl;

    private String bodyUrl;


    @Builder
    public Article(Long articleId, String title, String hashTag, String thumbnailUrl, String bodyUrl) {
        this.articleId = articleId;
        this.title = title;
        this.hashTag = hashTag;
        this.thumbnailUrl = thumbnailUrl;
        this.bodyUrl = bodyUrl;
    }
}
