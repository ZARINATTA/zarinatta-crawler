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

    // 출발
    @Column(nullable = false)
    private String departDate;

    @Column(nullable = false)
    private String departTime;

    @Column(nullable = false)
    private String departStation;

    // 도착
    @Column(nullable = false)
    private String arriveTime;

    @Column(nullable = false)
    private String arriveStation;

    @Column(nullable = false)
    private String price;

    @Column
    private boolean soldOut;

    @Builder
    public Ticket(String ticketType, String departDate, String departTime, String departStation, String arriveTime, String arriveStation, String price, boolean soldOut) {
        this.ticketType = ticketType;
        this.departDate = departDate;
        this.departTime = departTime;
        this.departStation = departStation;
        this.arriveTime = arriveTime;
        this.arriveStation = arriveStation;
        this.price = price;
        this.soldOut = soldOut;
    }
}
