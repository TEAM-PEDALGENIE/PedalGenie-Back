package com.pedalgenie.pedalgenieback.domain.image.presentation;

import com.pedalgenie.pedalgenieback.domain.image.ImageDirectoryUrl;
import com.pedalgenie.pedalgenieback.domain.image.application.ImageService;
import com.pedalgenie.pedalgenieback.domain.image.dto.ImageResponse;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageService;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/images")
public class ImageController {

    private final ImageService imageService;
    private final ProductImageService productImageSerivce;

    // 상품 이미지 db에 저장
    @PostMapping("/products/{productId}")
    public ResponseEntity<ResponseTemplate<ImageResponse>> uploadPrducts(
            @PathVariable Long productId,
            @RequestPart(required = false, value = "image") MultipartFile imageFile
            ){

        String url = imageService.save(imageFile, ImageDirectoryUrl.PRODUCT_DIRECTORY); // 타깃디렉토리

        productImageSerivce.saveProductImage(productId, url);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED
                ,true,"상품 이미지 저장 성공", ImageResponse.from(url));

    }
}
