package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatLookingFor {
    SEAT("좌석"),
    STANDING_SEAT("입석 + 좌석"),
    NOTFOUND("필요 없음");

    private final String text;
}
