package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.SeatState;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @Column(nullable = false)
    private boolean isTimeOut;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatState wantFirstClass;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatState wantNormalSeat;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatState wantBabySeat;

    @Column
    private boolean waitingSoldOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BookMark(boolean isTimeOut, SeatState wantFirstClass, SeatState wantNormalSeat, SeatState wantBabySeat, boolean waitingSoldOut, Ticket ticket, User user) {
        this.isTimeOut = isTimeOut;
        this.wantFirstClass = wantFirstClass;
        this.wantNormalSeat = wantNormalSeat;
        this.wantBabySeat = wantBabySeat;
        this.waitingSoldOut = waitingSoldOut;
        this.ticket = ticket;
        this.user = user;
    }
}
