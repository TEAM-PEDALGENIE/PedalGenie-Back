package com.pedalgenie.pedalgenieback.domain.shop.presentation;

import com.pedalgenie.pedalgenieback.domain.image.ImageDirectoryUrl;
import com.pedalgenie.pedalgenieback.domain.image.application.ImageService;
import com.pedalgenie.pedalgenieback.domain.shop.application.ShopQueryService;
import com.pedalgenie.pedalgenieback.domain.shop.application.ShopService;
import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopImageRequest;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopResponse;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopsResponses;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.ShopCreateResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShopController {

    private final ShopQueryService shopQueryService;
    private final TokenProvider tokenProvider;
    private final ImageService imageService;
    private final ShopService shopService;

    @Operation(summary = "매장 목록 조회")
    @GetMapping("/shops")
    public ResponseEntity<ResponseTemplate<GetShopsResponses>> getShops(@RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        Long memberId = null;

        // 토큰이 있는 경우 memberId 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            memberId = tokenProvider.getMemberIdFromToken(token);
        }
        GetShopsResponses getShopsResponses = shopQueryService.readShops(memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK,true,"매장 목록 조회 성공", getShopsResponses);

    }
    @Operation(summary = "매장 상세 조회")
    @GetMapping("/shops/{id}")
    public ResponseEntity<ResponseTemplate<GetShopResponse>> getShopDetails(@PathVariable Long id,
                                                                            @RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        Long memberId = null;

        // 토큰이 있는 경우 memberId 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            memberId = tokenProvider.getMemberIdFromToken(token);
        }

        GetShopResponse response = shopQueryService.readShop(id, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "매장 상세 조회 성공", response);

    }
    @Operation(summary = "매장 등록")
    @PostMapping("/admin/shops")
    public ResponseEntity<ResponseTemplate<ShopCreateResponse>> createShop(@ModelAttribute ShopImageRequest request){
        String url = imageService.save(request.getImageUrl(), ImageDirectoryUrl.SHOP_DIRECTORY);

        ShopCreateResponse response = shopService.createShop(
                request.toCreateRequest(), url);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "매장 등록 성공", response);

    }


}
