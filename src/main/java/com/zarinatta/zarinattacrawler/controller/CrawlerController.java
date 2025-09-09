package com.zarinatta.zarinattacrawler.controller;


import com.zarinatta.zarinattacrawler.service.crawler.RealTimeSeatCrawler;
import com.zarinatta.zarinattacrawler.service.crawler.RealTimeSeatCrawlerV2;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.AvailableSeatCrawler;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.CrawlerServiceV1;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.JsoupSeatCrawlerV2;
import com.zarinatta.zarinattacrawler.service.crawler.legacy.RealTimeSeatCrawlerV1;
import com.zarinatta.zarinattacrawler.service.crawler.renew.RealTimeKorailCrawler;
import com.zarinatta.zarinattacrawler.service.crawler.renew.RealTimeKorailCrawlerV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/crawler")
public class CrawlerController {

    private final RealTimeKorailCrawler realTimeKorailCrawler;
    private final RealTimeKorailCrawlerV2 realTimeKorailCrawlerV2;
    private final CrawlerServiceV1 crawlerService;
    private final AvailableSeatCrawler availableSeatCrawler;
    private final RealTimeSeatCrawler realTimeSeatCrawler;
    private final RealTimeSeatCrawlerV1 realTimeSeatCrawlerV1;
    private final RealTimeSeatCrawlerV2 realTimeSeatCrawlerV2;
    private final JsoupSeatCrawlerV2 jsoupSeatCrawler;

    // ==== 리뉴얼 코레일 페이지 ====//
    @GetMapping("/realTime/renewal")
    public String startCrawling() {
        realTimeKorailCrawler.startCycle();
        return "ok";
    }

    @GetMapping("/realTime/renewal/v2")
    public String startCrawlingv2() {
        realTimeKorailCrawlerV2.startCycle();
        return "ok";
    }

    //==== 즐겨 찾기만 크롤링 ====//
    @GetMapping("/realTime")
    public String startCrawlingCycle() {
        realTimeSeatCrawler.startCycle();
        return "ok";
    }

    @GetMapping("/realTimeV2")
    public String startAsyncCrawlingCycle() {
        realTimeSeatCrawlerV2.startCycle();
        return "async ok";
    }

    /**
     * 즐겨찾기만 스레드 풀 없이 매번 바디를 만들어서 크롤링
     */
    @GetMapping("/jsoup")
    public String startCrawlingCycleWithOutPool() {
        jsoupSeatCrawler.startCycle();
        return "ok";
    }

    //==== Legacy ====//
    //==== 전체 크롤링 ====//
    /**
     * 1 페이지만 긁어와서 저장
     */
    @GetMapping("/fetch")
    public String updateAllTicketByCrawling() {
        crawlerService.crawlerData_OnceADay();
        return "ok";
    }

    /**
     * 1페이지씩 넘어가면서 저장
     */
    @GetMapping("/realTime1")
    public String testCrawler1() {
        realTimeSeatCrawlerV1.getTicketList();
        return "ok";
    }

    /**
     * 1페이지씩 넘어가면서 저장
     */
    @GetMapping("/realTime2")
    public String testCrawler2() {
        availableSeatCrawler.getTicketList();
        return "ok";
    }
}
