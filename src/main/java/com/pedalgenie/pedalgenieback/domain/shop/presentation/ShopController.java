package com.pedalgenie.pedalgenieback.domain.shop.presentation;

import com.pedalgenie.pedalgenieback.domain.shop.application.ShopQueryService;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopResponse;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.GetShopsResponses;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final ShopQueryService shopQueryService;

    @Operation(summary = "매장 목록 조회")
    @GetMapping
    public ResponseEntity<ResponseTemplate<GetShopsResponses>> getShops(){

        GetShopsResponses getShopsResponses = shopQueryService.readShops();
        return ResponseTemplate.createTemplate(HttpStatus.OK,true,"매장 목록 조회 성공", getShopsResponses);

    }
    @Operation(summary = "매장 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTemplate<GetShopResponse>> getShopDetails(@PathVariable Long id){
        GetShopResponse response = shopQueryService.readShop(id);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true, "매장 상세 조회 성공", response);

    }


}
