package com.zarinatta.zarinattacrawler.service.api;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import com.zarinatta.zarinattacrawler.repository.FailedTicketLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final int CONN_TIMEOUT_VALUE = 3000;
    private final int READ_TIMEOUT_VALUE = 5000;
    private final int RETRY_COUNT = 3;
    private final int DELAY_TIME = 2000;

    private final FailedTicketLogRepository failedTicketLogRepository;

    @Retryable(retryFor = {IOException.class, RuntimeException.class},
            maxAttempts = RETRY_COUNT,
            backoff = @Backoff(delay = DELAY_TIME, multiplier = 2.0),
            recover = "recover")
    public StringBuilder callTrainApi(URL url) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setConnectTimeout(CONN_TIMEOUT_VALUE);
            conn.setReadTimeout(READ_TIMEOUT_VALUE);

            int responseCode = conn.getResponseCode();

            if (responseCode >= 500) {
                String errorBody = readStream(conn.getErrorStream());
                log.error("[ApiService] 5xx 에러 발생 - URL: {}, Code: {}, Reason: {}", url, responseCode, errorBody);
                throw new IOException("[ApiService] Server Error " + responseCode + " : " + errorBody);
            }
            if (responseCode >= 400) {
                String errorBody = readStream(conn.getErrorStream());
                log.error("[ApiService] 4xx 에러 발생: - URL: {}, Code: {}, Reason: {}", url, responseCode, errorBody);
                return new StringBuilder("{}");
            }
            String successBody = readStream(conn.getInputStream());
            return new StringBuilder(successBody);
        } catch (IOException e) {
            log.warn("[ApiService] 재시도 예정 - URL: {}, 사유: {}", url, e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Recover
    @Transactional
    public StringBuilder recover(Exception e, URL url) {
        log.error("[ApiService] 최종 실패 - URL : {}", url);
        log.error("[ApiService] 원본 Exception: ", e);

        String requestUrl = url.toString();
        String errorMessage = e.getMessage();
        LocalDateTime now = LocalDateTime.now();

        failedTicketLogRepository.findByRequestUrlAndIsSolved(requestUrl, false)
                .ifPresentOrElse(
                        existingLog -> {
                            existingLog.increaseRetryCount(errorMessage);
                        },
                        () -> {
                            FailedTicketLog errorLog = FailedTicketLog.builder()
                                    .requestUrl(requestUrl)
                                    .failMessage(errorMessage)
                                    .isSolved(false)
                                    .retryCount(0)
                                    .failedAt(now)
                                    .build();
                            failedTicketLogRepository.save(errorLog);
                        }
                );
        return new StringBuilder();
    }

    private String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}