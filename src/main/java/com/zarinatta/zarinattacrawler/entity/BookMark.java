package com.zarinatta.zarinattacrawler.entity;

import com.zarinatta.zarinattacrawler.enums.BookMarkStatus;
import com.zarinatta.zarinattacrawler.enums.SeatLookingFor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOKMARK_ID")
    private Long id;

    @Column(name = "IS_SENT", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private boolean isSent;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookMarkStatus status;

    @Column(name = "WANT_FIRST_CLASS", nullable = false)
    private boolean wantFirstClass;

    @Column(name = "WANT_NORMAL_SEAT", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatLookingFor wantNormalSeat;

    @Column(name = "WANT_BABY_SEAT", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatLookingFor wantBabySeat;

    @Column(name = "WANT_WAITING_RESERVATION", columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private boolean wantWaitingReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Builder
    public BookMark(boolean isSent, BookMarkStatus status, boolean wantFirstClass, SeatLookingFor wantNormalSeat, SeatLookingFor wantBabySeat, boolean wantWaitingReservation, Ticket ticket, User user) {
        this.isSent = isSent;
        this.status = status;
        this.wantFirstClass = wantFirstClass;
        this.wantNormalSeat = wantNormalSeat;
        this.wantBabySeat = wantBabySeat;
        this.wantWaitingReservation = wantWaitingReservation;
        this.ticket = ticket;
        this.user = user;
    }

    public void messageIsSent() {
        this.isSent = true;
    }
}
