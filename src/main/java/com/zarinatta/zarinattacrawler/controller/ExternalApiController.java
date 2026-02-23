package com.zarinatta.zarinattacrawler.controller;

import com.zarinatta.zarinattacrawler.service.api.TicketScheduler;
import com.zarinatta.zarinattacrawler.service.api.legacy.TrainInfoApiServiceV1;
import com.zarinatta.zarinattacrawler.service.api.legacy.TrainInfoApiTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

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
        ticketScheduler.getTrainSchedule();
        return "ok";
    }

    @GetMapping("/trainInfo/range")
    public String callTrainInfoApiWithRange(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ticketScheduler.getTicketByRange(startDate, endDate);
        return "%s ~ %s 기간의 열차 정보를 수집합니다.".formatted(startDate, endDate);
    }

    /**
     * ============== Legacy API ===============
     */
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
