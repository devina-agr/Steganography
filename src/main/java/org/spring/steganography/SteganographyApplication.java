package org.spring.steganography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SteganographyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteganographyApplication.class, args);
    }

}
