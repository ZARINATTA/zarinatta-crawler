package com.zarinatta.zarinattacrawler.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticketType;

    @Column(nullable = false)
    private String arriveDate;

    @Column(nullable = false)
    private String arriveTime;

    @Column(nullable = false)
    private String arriveStation;

    @Column(nullable = false)
    private String departTime;

    @Column(nullable = false)
    private String departStation;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private boolean soldOut;

    @Builder
    public Ticket(String ticketType, String arriveDate, String arriveTime, String arriveStation, String departTime, String departStation, String price, boolean soldOut) {
        this.ticketType = ticketType;
        this.arriveDate = arriveDate;
        this.arriveTime = arriveTime;
        this.arriveStation = arriveStation;
        this.departTime = departTime;
        this.departStation = departStation;
        this.price = price;
        this.soldOut = soldOut;
    }
}
