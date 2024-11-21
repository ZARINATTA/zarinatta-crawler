package com.zarinatta.zarinattacrawler.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "USER_NICK", nullable = false)
    private String userNick;

    @Column(name = "USER_PHONE", nullable = false)
    private String userPhoneNumber;

    @Column(name = "USER_DEVICE_TOKEN")
    private String userDeviceToken;
}
