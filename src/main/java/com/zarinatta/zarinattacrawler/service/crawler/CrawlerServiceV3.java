package com.zarinatta.zarinattacrawler.service.crawler;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrawlerServiceV3 {
    private final TicketRepository ticketRepository;
    private final CloseableHttpClient httpClient;
    private final List<HttpPost> httpPostRequests;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Transactional
    public void crawlerData_OnceADay() {
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int index = 0; index < 1764; index++) {
            HttpPost post = httpPostRequests.get(index);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    byte[] bytes = response.getEntity().getContent().readAllBytes();
                    String responseBody = new String(bytes, StandardCharsets.UTF_8);
                    Document document = Jsoup.parse(responseBody);
                    EntityUtils.consume(response.getEntity());
                    Elements table = document.select("tr");
                    for (Element ticket : table) {
                        Elements rows = ticket.select("td");
                        List<String> ticketInfo = rows.eachText();
                        List<String> ticketSeat = ticket.select("td img").eachAttr("alt");
                        String ticketPrice = (rows.select("td > div > strong").text());
                        if (ticketInfo.size() > 4 && !ticketInfo.get(1).startsWith("SRT")) {
                            ticketRepository.save(Ticket.builder()
                                    .ticketType(ticketInfo.get(1))
                                    .departDate(ticketInfo.get(2))
                                    .arriveTime(ticketInfo.get(2))
                                    .arriveStation(ticketInfo.get(2))
                                    .departTime(ticketInfo.get(3))
                                    .departStation(ticketInfo.get(3))
                                    .price(ticketPrice)
                                    .soldOut(ticketSeat.get(0).equals("좌석매진")).build());
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join(); // 모든 비동기 작업이 완료될 때까지 대기

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }
}
