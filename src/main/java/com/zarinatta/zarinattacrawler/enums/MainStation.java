package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MainStation {
    GANAM("가남"),
    GAPYEONG("가평"),
    GANGNEUNG("강릉"),
    GYEONGJU("경주"),
    GONGJU("공주"),
    GWANGJU_SONGJEONG("광주송정"),
    GWANGJU("광주"),
    GUMI("구미"),
    GUPO("구포"),
    GUNSAN("군산"),
    DONGDAEGU("동대구"),
    DAEJEON("대전"),
    MASAN("마산"),
    MOKPO("목포"),
    BUSAN("부산"),
    SEO_UL("서울"),
    SEO_DAEGU("서대구"),
    SUWON("수원"),
    SUNCHEON("순천"),
    SINHAE_UNDAE("신해운대"),
    ASAN("아산"),
    ANYANG("안양"),
    ANDONG("안동"),
    YEOSU_EXPO("여수EXPO"),
    YEONGDEUNGPO("영등포"),
    YEONGJU("영주"),
    IKSAN("익산"),
    INCHEON_GONGHANG_T1("인천공항T1"),
    INCHEON_GONGHANG_T2("인천공항T2"),
    JEONJU("전주"),
    CHEONAN("천안"),
    CHEONAN_ASAN("천안아산"),
    CHEONGLYANG_RI("청량리"),
    CHUNCHEON("춘천"),
    TAEBAEK("태백"),
    PANGYO_GYEONGGI("판교(경기)"),
    PYEONGCHANG("평창"),
    PYEONGTAEK("평택"),
    POHANG("포항"),
    HADONG("하동"),
    HAMAN("함안"),
    HONGSEONG("홍성");


    private final String name;
}
