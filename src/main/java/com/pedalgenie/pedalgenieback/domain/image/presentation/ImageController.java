package com.pedalgenie.pedalgenieback.domain.image.presentation;

import com.pedalgenie.pedalgenieback.domain.image.ImageDirectoryUrl;
import com.pedalgenie.pedalgenieback.domain.image.application.ImageService;
import com.pedalgenie.pedalgenieback.domain.image.dto.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageResponse> upload(

            @RequestPart(required = false, value = "image") MultipartFile imageFile
            ){

        String url = imageService.save(imageFile, ImageDirectoryUrl.PRODUCT_DIRECTORY); // 타깃디렉토리
        return ResponseEntity.created(URI.create(url)).body(ImageResponse.from(url));

    }
}
