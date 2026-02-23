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
public class SmsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SMS_MESSAGE_ID")
    private Long id;

    @Column(name = "MESSAGE_CONTENT", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "BOOKMARK_ID", nullable = false)
    private Long bookMarkId;

    @Column(name = "IS_SENT", nullable = false)
    private boolean isSent;

    @Column(name = "SNS_MESSAGE_ID")
    private String snsMessageId;

    @Column(name = "SENT_AT")
    private LocalDateTime sentAt;

    @Builder
    public SmsMessage(String messageContent, String phoneNumber, Long bookMarkId, boolean isSent, String snsMessageId, LocalDateTime sentAt) {
        this.messageContent = messageContent;
        this.phoneNumber = phoneNumber;
        this.bookMarkId = bookMarkId;
        this.isSent = isSent;
        this.snsMessageId = snsMessageId;
        this.sentAt = LocalDateTime.now();
    }

    public static SmsMessage success(String messageContent, String phoneNumber, BookMark bookMark, String snsMessageId) {
        return SmsMessage.builder()
                .messageContent(messageContent)
                .phoneNumber(phoneNumber)
                .bookMarkId(bookMark.getId())
                .isSent(true)
                .snsMessageId(snsMessageId)
                .build();
    }



}
