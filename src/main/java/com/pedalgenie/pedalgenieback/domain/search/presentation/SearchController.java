package com.pedalgenie.pedalgenieback.domain.search.presentation;

import com.pedalgenie.pedalgenieback.domain.search.dto.SearchResponse;
import com.pedalgenie.pedalgenieback.domain.search.application.SearchService;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "상품 또는 매장 검색 성공")
    @GetMapping
    public ResponseEntity<ResponseTemplate<SearchResponse>> search(@RequestParam String keyword,HttpServletRequest request) {
        Long memberId = null;
        // 쿠키에서 리프레시 토큰 추출 및 유효성 검증
        String refreshToken = tokenProvider.getRefreshTokenFromRequest(request);
        Boolean isValid = tokenProvider.isVaildRefreshToken(refreshToken);

        // 리프레시 토큰이 유효할 때 memberId 추출
        if(isValid) {
            memberId = tokenProvider.getMemberIdFromToken(refreshToken);
        }
        SearchResponse searchResponse = searchService.search(keyword, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "상품 또는 매장 검색 성공", searchResponse);
    }
}
