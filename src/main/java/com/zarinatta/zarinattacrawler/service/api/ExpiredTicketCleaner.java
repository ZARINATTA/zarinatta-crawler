package com.zarinatta.zarinattacrawler.service.api;

import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpiredTicketCleaner {
    private final TicketRepository ticketRepository;
    private final BookMarkRepository bookMarkRepository;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void deleteExpiredData() {
        // 1. 삭제 대상 날짜 설정 (10일 전)
        String targetDate = LocalDate.now().minusDays(10).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int chunkSize = 2000; // 한 번에 지울 개수
        long totalDeleted = 0;

        log.info("========= [스케쥴러] 만료 데이터 삭제 (기준: {} 이전) =========", targetDate);

        while (true) {
            Integer deletedInChunk = transactionTemplate.execute(status -> {
                // 2. 삭제할 대상 ID 조회
                Pageable pageable = PageRequest.of(0, chunkSize);
                List<Long> targetIds = ticketRepository.findIdsByDepartDateBefore(targetDate, pageable);
                if (targetIds.isEmpty()) {
                    return 0;
                }
                // 3. 연관된 즐겨찾기 먼저 삭제
                bookMarkRepository.deleteByTicketIdIn(targetIds);
                // 4. 티켓 삭제
                ticketRepository.deleteByIdIn(targetIds);
                return targetIds.size();
            });
            if (deletedInChunk == null || deletedInChunk == 0) {
                break;
            }
            totalDeleted += deletedInChunk;
            log.info(">> {}건 삭제 완료 (누적: {}건)...", deletedInChunk, totalDeleted);
        }

        log.info("========= [스케쥴러] 총 {}건 삭제 완료 =========", totalDeleted);
    }

}
