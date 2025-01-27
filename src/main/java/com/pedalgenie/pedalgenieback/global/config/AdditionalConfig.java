package com.pedalgenie.pedalgenieback.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AwsS3Credentials.class,
        FCMProperties.class
})
public class AdditionalConfig {
}
