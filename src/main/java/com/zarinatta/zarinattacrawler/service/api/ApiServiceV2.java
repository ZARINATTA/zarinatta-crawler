package com.zarinatta.zarinattacrawler.service.api;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import com.zarinatta.zarinattacrawler.repository.FailedTicketLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.util.Timeout;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiServiceV2 {

    private final int CONN_TIMEOUT_VALUE = 3000;
    private final int READ_TIMEOUT_VALUE = 5000;
    private final int RETRY_COUNT = 3;
    private final int DELAY_TIME = 2000;

    private final CloseableHttpClient httpClient;
    private final FailedTicketLogRepository failedTicketLogRepository;

    @Retryable(retryFor = {IOException.class, RuntimeException.class},
            maxAttempts = RETRY_COUNT,
            backoff = @Backoff(delay = DELAY_TIME, multiplier = 2.0),
            recover = "recover")
    public StringBuilder callTrainApi(URL url) throws IOException {
        HttpGet request = new HttpGet(url.toString());
        request.setHeader("Content-type", "application/json");
        request.setConfig(RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(CONN_TIMEOUT_VALUE))
                .setResponseTimeout(Timeout.ofMilliseconds(READ_TIMEOUT_VALUE))
                .build());

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int responseCode = response.getCode();

            if (responseCode >= 500) {
                String errorBody = readResponseBody(response);
                log.error("[ApiService] 5xx 에러 발생 - URL: {}, Code: {}, Reason: {}", url, responseCode, errorBody);
                throw new IOException("[ApiService] Server Error " + responseCode + " : " + errorBody);
            }
            if (responseCode >= 400) {
                String errorBody = readResponseBody(response);
                log.error("[ApiService] 4xx 에러 발생: - URL: {}, Code: {}, Reason: {}", url, responseCode, errorBody);
                return new StringBuilder("{}");
            }
            return new StringBuilder(readResponseBody(response));
        } catch (IOException e) {
            log.warn("[ApiService] 재시도 예정 - URL: {}, 사유: {}", url, e.getMessage());
            throw e;
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

    private String readResponseBody(CloseableHttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            return "";
        }
        return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    }
}
