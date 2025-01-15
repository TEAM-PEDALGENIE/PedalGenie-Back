package com.pedalgenie.pedalgenieback.domain.article.presentation;

import com.pedalgenie.pedalgenieback.domain.article.application.ArticleService;
import com.pedalgenie.pedalgenieback.domain.article.dto.request.ArticleImageRequestDto;
import com.pedalgenie.pedalgenieback.domain.article.dto.response.ArticleResponseDto;
import com.pedalgenie.pedalgenieback.domain.image.ImageDirectoryUrl;
import com.pedalgenie.pedalgenieback.domain.image.application.ImageService;
import com.pedalgenie.pedalgenieback.domain.member.entity.MemberRole;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Article api", description = "아티클 작성, 삭제, 목록 및 상세 조회 기능을 포함합니다.")
public class ArticleController {
    private final ImageService imageService;
    private final ArticleService articleService;
    private final TokenProvider tokenProvider;

    @Operation(summary="어드민 - 아티클 작성")
    @PostMapping("/admin/articles")
    public ResponseEntity<ResponseTemplate<ArticleResponseDto>> createArticle(
            @ModelAttribute ArticleImageRequestDto request
    ) {
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }

        // 이미지 파일 저장
        String thumbnailUrl = imageService.save(request.getThumbnail(), ImageDirectoryUrl.ARTICLE_DIRECTORY);
        String bodyUrl = imageService.save(request.getBody(), ImageDirectoryUrl.ARTICLE_DIRECTORY);

        // 아티클 저장
        ArticleResponseDto responseDto = articleService.createArticle(
                request.getTitle(),
                request.getHashTag(),
                request.getProductIds(),
                thumbnailUrl,
                bodyUrl
        );

        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "아티클 생성 성공", responseDto);
    }

    @Operation(summary="어드민 - 아티클 목록 조회")
    @GetMapping("/admin/articles")
    public ResponseEntity<ResponseTemplate<List<ArticleResponseDto>>> getAdminArticleList() {
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        // 아티클 목록 조회
        List<ArticleResponseDto> articleList = articleService.getAdminArticleList();

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "어드민 아티클 목록 조회 성공", articleList);
    }

    @Operation(summary="어드민 - 아티클 삭제")
    @DeleteMapping("/admin/articles/{articleId}")
    public ResponseEntity<ResponseTemplate<List<ArticleResponseDto>>> deleteArticle(@PathVariable Long articleId) {
        // 어드민이 아닐 때 예외처리
        if (!AuthUtils.getCurrentRole().equals(MemberRole.ADMIN.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ROLE);
        }
        // 아티클 삭제
        articleService.deleteArticle(articleId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "아티클 삭제 성공", null);
    }

    @Operation(summary="아티클 목록 조회")
    @GetMapping("/api/articles")
    public ResponseEntity<ResponseTemplate<List<ArticleResponseDto>>> getArticleList() {
        // 아티클 목록 조회
        List<ArticleResponseDto> articleList = articleService.getArticleList();

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "아티클 목록 조회 성공", articleList);
    }


    @Operation(summary="아티클 상세 조회")
    @GetMapping("/api/articles/{articleId}")
    public ResponseEntity<ResponseTemplate<ArticleResponseDto>> getArticle(@PathVariable Long articleId,
                                                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Long memberId = null;

        // 토큰이 있는 경우 memberId 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            memberId = tokenProvider.getMemberIdFromToken(token);
        }
        // 아티클 상세 조회
        ArticleResponseDto responseDto = articleService.getArticle(articleId, memberId);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "아티클 상세 조회 성공", responseDto);
    }

}
