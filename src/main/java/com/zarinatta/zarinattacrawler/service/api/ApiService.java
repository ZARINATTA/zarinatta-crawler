package com.zarinatta.zarinattacrawler.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiService {
    private final int OK = 200;
    private final int REDIRECT = 300;

    private final int TIMEOUT_VALUE = 1;
    private final int RETRY_COUNT = 3;
    private final int DELAY_TIME = 5000;

    @Retryable(retryFor = {SocketTimeoutException.class},
            maxAttempts = RETRY_COUNT,
            backoff = @Backoff(delay = DELAY_TIME),
            recover = "recover")
    public StringBuilder callTrainApi(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setConnectTimeout(TIMEOUT_VALUE);
        // 호출 결과 파싱
        BufferedReader rd;
        int responseCode = conn.getResponseCode();
        if (responseCode >= OK && responseCode <= REDIRECT) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb;
    }

    @Recover
    public StringBuilder recover(SocketTimeoutException e, URL url) {
        log.error("3회 호출 에도 응답 없는 호출 : {}", url);
        return new StringBuilder();
    }
}
