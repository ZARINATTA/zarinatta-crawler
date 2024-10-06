package com.zarinatta.zarinattacrawler.sns;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsManager {

    private final SnsClient snsClient;
    private final BookMarkRepository bookMarkRepository;

    public void sendSns(String message, String phoneNumber) {
        long startTime = System.currentTimeMillis();
        PublishRequest smsMessage = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse publish = snsClient.publish(smsMessage);
        log.info("messageId: {} , body: {} ", publish.messageId(), publish);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("문자 보내는데 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    @Async
    @Transactional
    public CompletableFuture<Void> sendSnsForBookMark(String message, String phoneNumber, BookMark bookMark) {
        long startTime = System.currentTimeMillis();
        try {
            PublishRequest smsMessage = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();
            PublishResponse publish = snsClient.publish(smsMessage);
            bookMark.messageIsSent();
            bookMarkRepository.flush();
            log.info("messageId: {} , body: {} ", publish.messageId(), publish);
        } catch (Exception e) {
            log.error("문자 전송 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("문자 보내는데 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
        return CompletableFuture.completedFuture(null);
    }
}
