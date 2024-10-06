package com.zarinatta.zarinattacrawler.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(name = "USER_ID")
    private String id;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "USER_NICK")
    private String userNick;

    @Column(name = "USER_PHONE")
    @Nullable
    private String userPhoneNumber;

    @Column(name = "USER_DEVICE_TOKEN")
    @Nullable
    private String userDeviceToken;
}
