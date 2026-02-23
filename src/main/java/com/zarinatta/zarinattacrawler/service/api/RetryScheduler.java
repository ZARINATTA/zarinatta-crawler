package com.zarinatta.zarinattacrawler.service.api;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import com.zarinatta.zarinattacrawler.repository.FailedTicketLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final TicketScheduler ticketScheduler;
    private final FailedTicketLogRepository failedTicketLogRepository;

    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    public void retryFailedTickets() {
        List<FailedTicketLog> unsolvedFailers = failedTicketLogRepository.findAllUnsolvedFailer();
        log.info("[RetryScheduler] 재시도 대상 티켓 수: {}", unsolvedFailers.size());

        for (FailedTicketLog unsolvedFailer : unsolvedFailers) {
            try {
                ticketScheduler.processSingleRequest(URI.create(unsolvedFailer.getRequestUrl()).toURL());
                failedTicketLogRepository.updateIsSolvedById(unsolvedFailer.getId());
            } catch (MalformedURLException e) {
                log.error("[RetryScheduler] 잘못된 URL: {}", unsolvedFailer.getRequestUrl());
            } catch (RuntimeException e) {
                log.error("[RetryScheduler] 재시도 실패 - URL: {}, 사유: {}", unsolvedFailer.getRequestUrl(), e.getMessage());
            }
        }
    }
}