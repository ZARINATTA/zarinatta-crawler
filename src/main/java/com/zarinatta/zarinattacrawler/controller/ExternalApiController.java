package com.zarinatta.zarinattacrawler.controller;

import com.zarinatta.zarinattacrawler.service.api.TicketScheduler;
import com.zarinatta.zarinattacrawler.service.api.legacy.TrainInfoApiServiceV1;
import com.zarinatta.zarinattacrawler.service.api.legacy.TrainInfoApiTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static java.time.LocalDateTime.now;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/external")
public class ExternalApiController {

    private final TicketScheduler ticketScheduler;
    private final TrainInfoApiTest trainInfoApiTest;
    private final TrainInfoApiServiceV1 trainInfoApiServiceV1;

    @GetMapping("/trainInfo")
    public String callTrainInfoApi() {
        long startTime = System.currentTimeMillis();
        ticketScheduler.getTrainSchedule();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
        return "ok";
    }

    @GetMapping("/mono/trainInfo")
    public String callTrainInfoApiMono() {
        trainInfoApiServiceV1.getTrainInfo();
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

    @GetMapping("/sentry/test")
    public void sentryTest(){
        throw new RuntimeException("Sentry 테스트 - timestamp: " + now());
    }
}
