package com.zarinatta.zarinattacrawler.controller;


import com.zarinatta.zarinattacrawler.service.crawler.*;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.AvailableSeatCrawler;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.CrawlerServiceV1;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.JsoupSeatCrawler;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.RealTimeSeatCrawlerV1;
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
    private final RealTimeSeatCrawlerV1 realTimeSeatCrawlerV1;
    private final JsoupSeatCrawler jsoupSeatCrawler;
    private final RealTimeSeatCrawler realTimeSeatCrawler;

    @GetMapping("/realTime")
    public String startCrawlingCycle() {
        realTimeSeatCrawler.startCycle();
        return "ok";
    }

    // legacy
    @GetMapping("/fetch")
    public String testCrawler() {
        crawlerService.crawlerData_OnceADay();
        return "ok";
    }

    @GetMapping("/realTime1")
    public String testCrawler1() {
        realTimeSeatCrawlerV1.getTicketList();
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
