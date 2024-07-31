package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatState {
    SOLD_OUT("매진"),
    STANDING_SEAT("입석 + 좌석"),
    RESERVATION("예매 가능");

    private final String text;
}
