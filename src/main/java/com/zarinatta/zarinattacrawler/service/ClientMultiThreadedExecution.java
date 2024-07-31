package com.zarinatta.zarinattacrawler.service;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMultiThreadedExecution {

    public static void main(String[] args) {
        // 커넥션 풀 설정
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100); // 최대 연결 수 설정
        connManager.setDefaultMaxPerRoute(20); // 라우트당 최대 연결 수 설정

        // HttpClient 생성
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .build()) {

            // 스레드 풀 생성
            ExecutorService executorService = Executors.newFixedThreadPool(10); // 10개의 스레드 풀 생성

            for (int i = 0; i < 100; i++) {
                int finalI = i;
                executorService.submit(() -> {
                    try {
                        sendGetRequest(httpClient, "https://jsonplaceholder.typicode.com/posts/" + finalI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES); // 스레드가 종료될 때까지 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendGetRequest(CloseableHttpClient httpClient, String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", "Mozilla/5.0");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println("Response Code: " + response.getCode());
            // 응답 내용 처리
        }
    }
}