package com.zarinatta.zarinattacrawler.controller;

import com.zarinatta.zarinattacrawler.service.api.TrainInfoApiServiceV2;
import com.zarinatta.zarinattacrawler.service.api.TrainInfoApiTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/external")
public class ExternalApiController {

    private final TrainInfoApiServiceV2 trainInfoApiService;
    private final TrainInfoApiTest trainInfoApiTest;

    @GetMapping("/trainInfo")
    public String callTrainInfoApi() {
        long startTime = System.currentTimeMillis();
        trainInfoApiService.getTrainInfo();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
        return "ok";
    }

    @GetMapping("/test")
    public String testApi() {
        try {
            trainInfoApiTest.test();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "ok";
    }
}
