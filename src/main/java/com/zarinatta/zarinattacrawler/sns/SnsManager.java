package com.zarinatta.zarinattacrawler.sns;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsManager {

    private final SnsClient snsClient;

    public void sendSns(String message, String phoneNumber) {
        PublishRequest smsMessage = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

        PublishResponse publish = snsClient.publish(smsMessage);
        log.info("messageId: {} , body: {} ", publish.messageId(), publish);
    }
}
