package com.zarinatta.zarinattacrawler.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MainStationCode {
    GANAM("NAT280090", "가남"),
    GAPYEONG("NAT140576", "가평"),
    GANGNEUNG("NAT601936", "강릉"),
    GYEONGJU("NATH13421", "경주"),
    GONGJU("NATH20438", "공주"),
    GWANGJU_SONGJEONG("NAT031857", "광주송정"),
    GWANGJU("NAT883012", "광주"),
    GUMI("NAT012775", "구미"),
    GUPO("NAT014281", "구포"),
    GUNSAN("NAT081388", "군산"),
    DONGDAEGU("NAT013271", "동대구"),
    DAEJEON("NAT011668", "대전"),
    MASAN("NAT880345", "마산"),
    MOKPO("NAT032563", "목포"),
    BUSAN("NAT014445", "부산"),
    SEO_UL("NAT010000", "서울"),
    SEO_DAEGU("NAT013189", "서대구"),
    SUWON("NAT010415", "수원"),
    SUNCHEON("NAT041595", "순천"),
    SINHAE_UNDAE("NAT750189", "신해운대"),
    ASAN("NAT080045", "아산"),
    ANYANG("NAT010239", "안양"),
    ANDONG("NAT022558", "안동"),
    YEOSU_EXPO("NAT041993", "여수EXPO"),
    YEONGDEUNGPO("NAT010091", "영등포"),
    YEONGJU("NAT022188", "영주"),
    IKSAN("NAT030879", "익산"),
    INCHEON_GONGHANG_T1("NATC10580", "인천공항T1"),
    INCHEON_GONGHANG_T2("NATC30058", "인천공항T2"),
    JEONJU("NAT040257", "전주"),
    CHEONAN("NAT010971", "천안"),
    CHEONAN_ASAN("NATH10960", "천안아산"),
    CHEONGLYANG_RI("NAT130126", "청량리"),
    CHUNCHEON("NAT140873", "춘천"),
    TAEBAEK("NAT650978", "태백"),
    PANGYO_GYEONGGI("NAT081240", "판교"),
    PYEONGCHANG("NATN10625", "평창"),
    PYEONGTAEK("NAT010754", "평택"),
    POHANG("NAT8B0351", "포항"),
    HADONG("NAT881460", "하동"),
    HAMAN("NAT880520", "함안"),
    HONGSEONG("NAT080622", "홍성");

    private final String code;
    private final String name;
}
