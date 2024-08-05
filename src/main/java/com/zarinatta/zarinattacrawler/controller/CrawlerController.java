package com.zarinatta.zarinattacrawler.controller;


import com.zarinatta.zarinattacrawler.service.crawler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/crawler")
public class CrawlerController {
    private final CrawlerServiceV1 crawlerService;
    private final AvailableSeatCrawler availableSeatCrawler;
    private final RealTimeSeatCrawler realTimeSeatCrawler;
    private final JsoupSeatCrawler jsoupSeatCrawler;

    @GetMapping("/fetch")
    public String testCrawler() {
        crawlerService.crawlerData_OnceADay();
        return "ok";
    }

    @GetMapping("/realTime")
    public String testCrawler1() {
        realTimeSeatCrawler.getTicketList();
        return "ok";
    }

    @GetMapping("/realTime2")
    public String testCrawler2() {
        availableSeatCrawler.getTicketList();
        return "ok";
    }

    @GetMapping("/realTime3")
    public String testCrawler3() throws IOException {
        jsoupSeatCrawler.getTicketList();
        return "ok";
    }
}
