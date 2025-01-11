package com.pedalgenie.pedalgenieback.domain.search;

import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<ResponseTemplate<SearchResponse>> search(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        {
            Long memberId = null;

            // 토큰이 있는 경우 memberId 추출
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                memberId = tokenProvider.getMemberIdFromToken(token);
            }

            SearchResponse searchResponse = searchService.search(keyword, memberId);
            return ResponseTemplate.createTemplate(HttpStatus.OK, true, "상품 또는 매장 검색 성공", searchResponse);
        }
    }
}
