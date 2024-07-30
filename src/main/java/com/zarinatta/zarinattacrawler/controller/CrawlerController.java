package com.zarinatta.zarinattacrawler.controller;


import com.zarinatta.zarinattacrawler.service.crawler.CrawlerServiceV3;
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
    private final CrawlerServiceV3 crawlerService;

    @GetMapping("/fetch")
    public String testCrawler() {
        crawlerService.crawlerData_OnceADay();
        return "ok";
    }
}
