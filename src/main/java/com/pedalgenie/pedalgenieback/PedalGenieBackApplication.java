package com.pedalgenie.pedalgenieback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = {
        net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class
}) // gRPC 보안 관련 자동 설정 제외
public class PedalGenieBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedalGenieBackApplication.class, args);
    }

}
