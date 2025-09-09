package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.StationCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private Long id;

    @Column(name = "TICKET_TYPE", nullable = false)
    private String ticketType;

    // 출발
    @Column(name = "DEPART_DATE", nullable = false)
    private String departDate;

    @Column(name = "DEPART_TIME", nullable = false)
    private String departTime;

    @Column(name = "DEPART_STATION", nullable = false)
    @Enumerated(EnumType.STRING)
    private StationCode departStation;

    // 도착
    @Column(name = "ARRIVE_TIME", nullable = false)
    private String arriveTime;

    @Column(name = "ARRIVE_STATION", nullable = false)
    @Enumerated(EnumType.STRING)
    private StationCode arriveStation;

    @Column(name = "PRICE", nullable = false)
    private String price;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<BookMark> bookMarks;

    @Builder
    public Ticket(String ticketType, String departDate, String departTime, StationCode departStation, String arriveTime, StationCode arriveStation, String price, List<BookMark> bookMarks) {
        this.ticketType = ticketType;
        this.departDate = departDate;
        this.departTime = departTime;
        this.departStation = departStation;
        this.arriveTime = arriveTime;
        this.arriveStation = arriveStation;
        this.price = price;
        this.bookMarks = bookMarks;
    }
}
