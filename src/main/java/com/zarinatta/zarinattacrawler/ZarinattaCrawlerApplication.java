package com.zarinatta.zarinattacrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ZarinattaCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZarinattaCrawlerApplication.class, args);
    }

}
