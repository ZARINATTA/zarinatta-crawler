package com.zarinatta.zarinattacrawler.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedTicketLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAILED_TICKET_LOG_ID")
    private Long id;

    @Column(name = "REQUEST_URL", nullable = false, columnDefinition = "TEXT")
    private String requestUrl;

    @Column(name = "FAIL_MESSAGE", nullable = false, columnDefinition = "TEXT")
    private String failMessage;

    @Column(name = "IS_SOLVED", columnDefinition = "boolean default false")
    private Boolean isSolved;

    @Column(name = "RETRY_COUNT")
    private Integer retryCount;

    @Column(name = "FAILED_AT", nullable = false)
    private LocalDateTime failedAt;

    @Builder
    public FailedTicketLog(String requestUrl, String failMessage, Boolean isSolved, Integer retryCount, LocalDateTime failedAt) {
        this.requestUrl = requestUrl;
        this.failMessage = failMessage;
        this.isSolved = isSolved;
        this.retryCount = retryCount;
        this.failedAt = failedAt;
    }
}