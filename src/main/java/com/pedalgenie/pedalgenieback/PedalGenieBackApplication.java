package com.pedalgenie.pedalgenieback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
public class PedalGenieBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedalGenieBackApplication.class, args);
    }

}
