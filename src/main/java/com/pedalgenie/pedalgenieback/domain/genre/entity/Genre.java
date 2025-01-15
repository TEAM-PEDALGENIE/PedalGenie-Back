package com.pedalgenie.pedalgenieback.domain.genre.entity;

import com.pedalgenie.pedalgenieback.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Genre extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @NotNull
    private String genreName;

    @Builder
    public Genre(Long genreId, String genreName){
        this.genreId = genreId;
        this.genreName = genreName;
    }
}
