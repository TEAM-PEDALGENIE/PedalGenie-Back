package com.pedalgenie.pedalgenieback.domain.image.application;

import org.springframework.web.multipart.MultipartFile;

public interface ImageClient {
    String upload(String name, MultipartFile file, String directory);
}
