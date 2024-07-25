package com.zarinatta.zarinattacrawler.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private LocalDate arriveDate;

    @Column(nullable = false)
    private LocalTime arriveTime;

    @Column(nullable = false)
    private String arriveStation;

    @Column(nullable = false)
    private LocalDate departTime;

    @Column(nullable = false)
    private LocalTime departStation;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean soldOut;

}
