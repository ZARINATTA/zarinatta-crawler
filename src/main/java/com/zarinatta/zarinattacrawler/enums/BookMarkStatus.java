package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookMarkStatus {

    SUCCESS("성공"), FAIL("실패"), UNKNOWN("아직 선택 안함");

    private final String status;
}
