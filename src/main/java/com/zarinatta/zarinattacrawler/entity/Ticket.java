package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.SeatState;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Column(nullable = false)
    private String ticketType;

    // 출발
    @Column(nullable = false)
    private String departDate;

    @Column(nullable = false)
    private String departTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StationCode departStation;

    // 도착
    @Column(nullable = false)
    private String arriveTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StationCode arriveStation;

    @Column(nullable = false)
    private String price;


    @Builder
    public Ticket(String ticketType, String departDate, String departTime, StationCode departStation, String arriveTime, StationCode arriveStation, String price) {
        this.ticketType = ticketType;
        this.departDate = departDate;
        this.departTime = departTime;
        this.departStation = departStation;
        this.arriveTime = arriveTime;
        this.arriveStation = arriveStation;
        this.price = price;
    }
}
