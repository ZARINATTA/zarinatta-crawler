package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StationSection {
    서울_경기("SEOUL_GYEONGGI"),
    대전_충청("DAEJEON_CHUNGCHEONG"),
    경상_부산_대구("GYEONGSANG"),
    강원("GANGWON"),
    전라_광주("JEOLLA");

    private final String englishName;
}
