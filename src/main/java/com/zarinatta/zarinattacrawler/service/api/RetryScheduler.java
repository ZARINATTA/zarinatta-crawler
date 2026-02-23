package com.zarinatta.zarinattacrawler.service.api;

import com.zarinatta.zarinattacrawler.entity.FailedTicketLog;
import com.zarinatta.zarinattacrawler.repository.FailedTicketLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final TicketScheduler ticketScheduler;
    private final FailedTicketLogRepository failedTicketLogRepository;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void retryFailedTickets() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("=========================================================");
        log.info("[RetryScheduler] 실패 티켓 재시도 - {}", LocalDateTime.now().format(formatter));
        log.info("=========================================================");

        List<FailedTicketLog> unsolvedFailers = failedTicketLogRepository.findAllUnsolvedFailer();
        log.info("[RetryScheduler] 재시도 대상 티켓 수: {}", unsolvedFailers.size());

        for (FailedTicketLog unsolvedFailer : unsolvedFailers) {
            try {
                ticketScheduler.processSingleRequest(URI.create(unsolvedFailer.getRequestUrl()).toURL());
                unsolvedFailer.markAsSolved();
            } catch (MalformedURLException e) {
                log.error("[RetryScheduler] 잘못된 URL: {}", unsolvedFailers.size());
                throw new RuntimeException(e);
            }
        }
    }
}