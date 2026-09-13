package com.zarinatta.zarinattacrawler.controller;

import com.zarinatta.zarinattacrawler.service.api.TicketSchedulerNormal;
import com.zarinatta.zarinattacrawler.service.api.TicketSchedulerPool;
import com.zarinatta.zarinattacrawler.service.api.legacy.v1.TrainInfoApiServiceV1;
import com.zarinatta.zarinattacrawler.service.api.legacy.v1.TrainInfoApiServiceV2;
import com.zarinatta.zarinattacrawler.service.api.legacy.v1.TrainInfoApiTest;
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

    private final TicketSchedulerNormal ticketScheduler;
    private final TicketSchedulerPool trainSchedulerV3;
    private final TrainInfoApiTest trainInfoApiTest;
    private final TrainInfoApiServiceV1 trainInfoApiServiceV1;
    private final TrainInfoApiServiceV2 trainInfoApiServiceV2;

    @GetMapping("/trainInfo")
    public String callTrainInfoApi() {
        ticketScheduler.getTrainSchedule();
        return "ok";
    }

    /**
     * 수동으로 특정 기간의 열차 시간표 정보를 가져와 DB에 저장
     */
    @GetMapping("/trainInfo/range")
    public String callTrainInfoApiWithRange(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ticketScheduler.getTicketByRange(startDate, endDate);
        return "%s ~ %s 기간의 열차 정보를 수집합니다.".formatted(startDate, endDate);
    }

    /**
     * 커넥션 풀을 사용하여 6일 뒤 열차 시간표 정보를 가져와 DB에 저장
     */
    @GetMapping("/trainInfo/pool")
    public String callTrainInfoApiTest() {
        trainSchedulerV3.getTrainSchedule();
        return "ok";
    }

    /**
     * ============== Legacy API ===============
     */
    @GetMapping("/mono/trainInfo")
    public String callTrainInfoApiMono() {
        trainInfoApiServiceV1.getTrainInfo();
        return "ok";
    }

    @GetMapping("/mono/trainInfoV2")
    public String callTrainInfoApiWithPool() {
        trainInfoApiServiceV2.getTrainInfo();
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
