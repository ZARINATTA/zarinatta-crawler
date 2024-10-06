package com.zarinatta.zarinattacrawler.entity;

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
    @Column(name = "bookmark_id")
    private Long id;

    @Column
    private boolean isSent;

    @Column
    private boolean wantFirstClass;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatLookingFor wantNormalSeat;

    @Column
    @Enumerated(EnumType.STRING)
    private SeatLookingFor wantBabySeat;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE", nullable = false)
    private boolean wantWaitingReservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public BookMark(boolean isSent, boolean wantFirstClass, SeatLookingFor wantNormalSeat, SeatLookingFor wantBabySeat, boolean wantWaitingReservation, Ticket ticket, User user) {
        this.isSent = isSent;
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
