package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatState {
    신청하기("예약 대기 신청 하기"),
    예약하기("좌석 예약 하기"),
    입좌석묶음예약("입석 + 좌석 묶음 예약"),
    유아동반객실("유아석");

    private final String text;
}
