package com.zarinatta.zarinattacrawler.service.crawler.renew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KorailResponseDto {
    @JsonProperty("h_msg_txt")
    private String messageText;

    @JsonProperty("strResult")
    private String resultStatus;

    @JsonProperty("trn_infos")
    private TrainInfos trainInfos;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class TrainInfos {
    @JsonProperty("trn_info")
    private List<TrainInfo> trainInfoList;
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class TrainInfo {
    @JsonProperty("h_dpt_rs_stn_nm")
    private String departureStation;

    @JsonProperty("h_arv_rs_stn_nm")
    private String arrivalStation;

    @JsonProperty("h_dpt_tm")
    private String departureTime;

    @JsonProperty("h_arv_tm")
    private String arrivalTime;

    @JsonProperty("h_spe_rsv_nm")
    private String specialSoldOut; // "좌석많음", "매진임박" : 예약 가능 / "-", "매진" : 예약 불가

    @JsonProperty("h_gen_rsv_nm")
    private String generalSoldOut; // "좌석많음", "매진임박", "매진“

    @JsonProperty("h_wait_rsv_flg")
    private String waitReservationFlag; // " 9" 면 예약대기 가능, "0" 이면 에약대기 불가능, "-2" 면 예약 대기 없는 기차

    @JsonProperty("h_yms_apl_flg")
    private String standingFlag; // "G" 면 입석 가능, "Y", "N" 면 입석 불가능

    public boolean isSpecialAvailable() {
        if (this.specialSoldOut.equals("-") || this.specialSoldOut.equals("매진")) {
            return false;
        } else if (this.specialSoldOut.equals("좌석많음") || this.specialSoldOut.equals("매진임박")) {
            return true;
        } else {
            throw new RuntimeException("NEW RESPONSE (specialSoldOut) :" + this.specialSoldOut);
        }
    }

    public boolean isGeneralAvailable() {
        if (this.generalSoldOut.equals("-") || this.generalSoldOut.equals("매진")) {
            return false;
        } else if (this.generalSoldOut.equals("좌석많음") || this.generalSoldOut.equals("매진임박")) {
            return true;
        } else {
            throw new RuntimeException("NEW RESPONSE (generalSoldOut) :" + this.generalSoldOut);
        }
    }

    public boolean isWaitAvailable() {
        String flag = this.waitReservationFlag.strip();
        if (flag.equals("0") || flag.equals("-2")) {
            return false;
        } else if (flag.equals("9")) {
            return true;
        } else {
            throw new RuntimeException("NEW RESPONSE (waitReservationFlag) :" + this.waitReservationFlag);
        }
    }

    public boolean isStandingAvailable() {
        if (this.standingFlag.equals("Y") || this.standingFlag.equals("N")) {
            return false;
        } else if (this.standingFlag.equals("G")) {
            return true;
        } else {
            throw new RuntimeException("NEW RESPONSE (standingFlag) :" + this.standingFlag);
        }
    }
}