package com.zarinatta.zarinattacrawler.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.pool.PoolStats;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionPoolMonitor {
    private final PoolingHttpClientConnectionManager connectionManager;

    // 5초마다 커넥션 풀 상태를 로그로 출력
    @Scheduled(fixedRate = 5000)
    public void logConnectionPoolStats() {
        PoolStats stats = connectionManager.getTotalStats();
        log.info("Connection Pool Stats - Active: {}, Available: {}, Leased: {}, Pending: {}, Max: {}",
                stats.getAvailable() + stats.getLeased(), // 현재 생성된 총 커넥션
                stats.getAvailable(), // 사용 가능한 유휴 커넥션
                stats.getLeased(),    // 현재 사용 중인 커넥션
                stats.getPending(),   // 커넥션을 얻기 위해 대기 중인 요청 수
                stats.getMax()        // 최대 커넥션 수
        );
    }
}
