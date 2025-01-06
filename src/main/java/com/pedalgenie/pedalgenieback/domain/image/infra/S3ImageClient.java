package com.pedalgenie.pedalgenieback.domain.image.infra;

import com.pedalgenie.pedalgenieback.domain.image.application.ImageClient;
import com.pedalgenie.pedalgenieback.global.config.AwsS3Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3ImageClient implements ImageClient {

    private final AwsS3Credentials awsS3Credentials;
    private final S3Uploader s3Uploader;

    public String upload(String fileName, MultipartFile file, String targetDirectory){
        String bucket = awsS3Credentials.getBucket(); // env에서 가져오는 값
        String envDirectory = awsS3Credentials.getEnv();
        String musaiDirectory = awsS3Credentials.getMusaiDirectory();

        // 예) musai/dev/product-image/{UUID}
        String route = musaiDirectory + envDirectory +targetDirectory +fileName;

        s3Uploader.upload(bucket, route, file);
        return awsS3Credentials.getImageUrl() + targetDirectory + fileName;

    }
}
