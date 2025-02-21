package com.zarinatta.zarinattacrawler.controller;

import com.zarinatta.zarinattacrawler.sns.SnsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/sns")
public class SnsController {

    private final SnsManager snsManager;

    /**
     * SNS 이용 해서 문자 보내는 테스트
     */
    @GetMapping("/send")
    public String sendSns() {
        long startTime = System.currentTimeMillis();
        snsManager.sendSns("AWS SNS 테스트", "+821058953445");
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
        return "ok";
    }
}
