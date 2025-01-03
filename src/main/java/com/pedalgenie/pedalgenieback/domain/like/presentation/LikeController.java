package com.pedalgenie.pedalgenieback.domain.like.presentation;

import com.pedalgenie.pedalgenieback.domain.like.service.LikeService;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.AuthUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@Tag(name = "Like api", description = "가게, 악기 좋아요 기능을 포함합니다.")
public class LikeController {

    private final LikeService likeService;

    // 악기 좋아요 생성
    @PostMapping("/products/{productId}")
    public ResponseEntity<ResponseTemplate<Object>> createProductLike(@PathVariable Long productId) {
        Long memberId = AuthUtils.getCurrentMemberId();

        likeService.createProductLike(productId, memberId);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "악기 좋아요 생성 성공", null);
    }

    // 가게 좋아요 생성
    @PostMapping("/shops/{shopId}")
    public ResponseEntity<ResponseTemplate<Object>> createShopLike(@PathVariable Long shopId) {
        Long memberId = AuthUtils.getCurrentMemberId();

        likeService.createShopLike(shopId, memberId);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED, true, "가게 좋아요 생성 성공", null);
    }

    // 악기 좋아요 삭제
    @DeleteMapping("/products/{productLikeId}")
    public ResponseEntity<ResponseTemplate<Object>> removeProductLike(@PathVariable Long productLikeId) {
        Long memberId = AuthUtils.getCurrentMemberId();

        likeService.removeProductLike(productLikeId, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.NO_CONTENT, true, "악기 좋아요 삭제 성공", null);
    }

    // 가게 좋아요 삭제
    @DeleteMapping("/likes/shops/{shopLikeId}")
    public ResponseEntity<ResponseTemplate<Object>> removeShopLike(@PathVariable Long shopLikeId) {
        Long memberId = AuthUtils.getCurrentMemberId();

        likeService.removeShopLike(shopLikeId, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.NO_CONTENT, true, "가게 좋아요 삭제 성공", null);
    }
}
