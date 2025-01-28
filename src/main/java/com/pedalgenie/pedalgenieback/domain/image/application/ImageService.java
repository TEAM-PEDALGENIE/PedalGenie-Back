package com.pedalgenie.pedalgenieback.domain.image.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageClient imageClient;

    public String save(MultipartFile image, String directory){
        if (image == null || image.isEmpty()) {
            return null;  // 이미지가 없으면 null을 리턴
        }

        UUID uuid = UUID.randomUUID(); // 파일 이름
        return imageClient.upload(uuid.toString(), image, directory);
    }
}
