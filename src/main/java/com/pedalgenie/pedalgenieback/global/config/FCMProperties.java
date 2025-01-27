package com.pedalgenie.pedalgenieback.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "firebase")
public class FCMProperties {
    private final ClassPathResource config;
    private final String scope;
}
