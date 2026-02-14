package com.zarinatta.zarinattacrawler.service.crawler.renew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.entity.User;
import com.zarinatta.zarinattacrawler.enums.SeatLookingFor;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import com.zarinatta.zarinattacrawler.sns.SnsManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeKorailCrawlerV3 {

    private final CloseableHttpClient httpClient;
    private final BookMarkRepository bookMarkRepository;
    private final SnsManager snsManager;
    private final ObjectMapper objectMapper;

    private static final String KORAIL_HOST = "smart.letskorail.com";
    private static final String SEARCH_PATH = "/classes/com.korail.mobile.seatMovie.ScheduleView";
    private static final String DEFAULT_USER_AGENT = "Dalvik/2.1.0 (Linux; U; Android 5.1.1; Nexus 4 Build/LMY48T)";

    // @Scheduled(fixedDelay = 30000)
    public void startCycle() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        int page = 0;
        int chunkSize = 100;

        // 현재 시간 이후의 예약 대기 목록 조회
        Page<BookMark> pageResult = bookMarkRepository.findChunkByAfterNow(
                dateFormatter.format(LocalDateTime.now()),
                timeFormatter.format(LocalDateTime.now()),
                PageRequest.of(page, chunkSize)
        );

        while (pageResult.hasContent()) {
            List<BookMark> bookMarkList = pageResult.getContent();
            // 동일한 티켓(출발지, 도착지, 시간)을 찾는 유저끼리 그룹화
            Map<Ticket, List<BookMark>> ticketBookMarkMap = makeBookMarkUserMap(bookMarkList);

            // 크롤링 실행
            realtimeSeatCrawler(ticketBookMarkMap);

            if (pageResult.hasNext()) {
                page++;
                pageResult = bookMarkRepository.findChunkByAfterNow(
                        dateFormatter.format(LocalDateTime.now()),
                        timeFormatter.format(LocalDateTime.now()),
                        PageRequest.of(page, chunkSize)
                );
            } else {
                break;
            }
        }
    }

    /**
     * 티켓별로 북마크한 유저들을 매핑
     */
    private Map<Ticket, List<BookMark>> makeBookMarkUserMap(List<BookMark> bookMarkList) {
        Map<Ticket, List<BookMark>> ticketBookMarkMap = new HashMap<>();
        for (BookMark bookMark : bookMarkList) {
            ticketBookMarkMap.computeIfAbsent(bookMark.getTicket(), k -> new ArrayList<>()).add(bookMark);
        }
        return ticketBookMarkMap;
    }

    private void realtimeSeatCrawler(Map<Ticket, List<BookMark>> ticketBookMarkMap) {
        for (Map.Entry<Ticket, List<BookMark>> ticketBookMarkSet : ticketBookMarkMap.entrySet()) {
            Ticket target = ticketBookMarkSet.getKey();

            // GET 요청 생성
            HttpGet httpGet = makeHttpGet(target.getDepartStation(), target.getArriveStation(), target.getDepartDate(), target.getDepartTime());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                KorailSearchResponse korailResponse = objectMapper.readValue(jsonResponse, KorailSearchResponse.class);

                if ("SUCC".equals(korailResponse.getResultStatus())) {
                    if (korailResponse.getTrainInfos() != null && korailResponse.getTrainInfos().getTrainInfoList() != null) {
                        TrainInfo realTimeTargetInfo = findTarget(target, korailResponse.getTrainInfos().getTrainInfoList());
                        if (realTimeTargetInfo != null) {
                            sendSMS(ticketBookMarkSet.getValue(), realTimeTargetInfo);
                        }
                    }
                } else {
                    log.error("Korail Error Response: {} (Code: {})", korailResponse.getErrorMessage(), korailResponse.getErrorCode());
                }
            } catch (IOException e) {
                log.error("IO Exception during crawling", e);
            } catch (Exception e) {
                log.error("Exception during crawling", e);
            }
        }
    }

    private HttpGet makeHttpGet(StationCode depart, StationCode arrive, String targetDate, String targetTime) {
        try {
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(KORAIL_HOST)
                    .setPath(SEARCH_PATH)
                    .addParameter("Device", "AD")
                    .addParameter("radJobId", "1")
                    .addParameter("selGoTrain", "109")
                    .addParameter("txtCardPsgCnt", "0")
                    .addParameter("txtGdNo", "")
                    .addParameter("txtGoAbrdDt", targetDate) // yyyyMMdd
                    .addParameter("txtGoEnd", arrive.name()) // 도착역
                    .addParameter("txtGoHour", targetTime + "00") // HHmmss
                    .addParameter("txtGoStart", depart.name()) // 출발역
                    .addParameter("txtJobDv", "")
                    .addParameter("txtMenuId", "11")
                    .addParameter("txtPsgFlg_1", "1") // 어른 1명 기준 조회
                    .addParameter("txtPsgFlg_2", "0")
                    .addParameter("txtPsgFlg_3", "0")
                    .addParameter("txtPsgFlg_4", "0")
                    .addParameter("txtPsgFlg_5", "0")
                    .addParameter("txtPsgFlg_8", "0")
                    .addParameter("txtSeatAttCd_2", "000")
                    .addParameter("txtSeatAttCd_3", "000")
                    .addParameter("txtSeatAttCd_4", "015")
                    .addParameter("txtTrnGpCd", "109")
                    .addParameter("Version", "190617001")
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("User-Agent", DEFAULT_USER_AGENT);
            return httpGet;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build URI", e);
        }
    }

    private TrainInfo findTarget(Ticket target, List<TrainInfo> trainInfoList) {
        for (TrainInfo trainInfo : trainInfoList) {
            if (trainInfo.getDepStation().equals(target.getDepartStation().name()) &&
                    trainInfo.getArrStation().equals(target.getArriveStation().name()) &&
                    trainInfo.getDepTime().startsWith(target.getDepartTime()) &&
                    trainInfo.getArrTime().startsWith(target.getArriveTime())) {
                return trainInfo;
            }
        }
        log.warn("findTarget - 티켓을 찾을수 없습니다. (targetId : {})", target.getId());
        return null;
    }

    public void sendSMS(List<BookMark> bookMarks, TrainInfo realTimeTargetInfo) {
        for (BookMark bookMark : bookMarks) {
            User user = bookMark.getUser();
            Ticket myTicket = bookMark.getTicket();
            boolean ticketExist = false;
            StringBuilder message = new StringBuilder();

            message.append("[자리나따] ").append(user.getUserNick()).append("님, ");
            // 시간 포맷팅 (HHmmss -> HH:mm)
            String depTimeStr = myTicket.getDepartTime();
            message.append(depTimeStr.substring(0, 2)).append(":").append(depTimeStr.substring(2)).append(" ");
            message.append(myTicket.getDepartStation()).append(" 출발 열차 ");

            // 특실 확인
            if (bookMark.isWantFirstClass() && realTimeTargetInfo.hasSpecialSeat()) {
                message.append("[특실]");
                ticketExist = true;
            }
            // 일반실 확인
            if (SeatLookingFor.SEAT.equals(bookMark.getWantNormalSeat()) && realTimeTargetInfo.hasGeneralSeat()) {
                message.append("[일반실]");
                ticketExist = true;
            }
            // 입석 + 좌석 확인
            if (SeatLookingFor.STANDING_SEAT.equals(bookMark.getWantNormalSeat()) &&
                    (realTimeTargetInfo.hasGeneralSeat() || realTimeTargetInfo.isStandingAvailable())) {
                message.append("[일반실/입석]");
                ticketExist = true;
            }
            // 예약 대기 확인
            if (bookMark.isWantWaitingReservation() && realTimeTargetInfo.hasWaitingList()) {
                message.append("[예약대기]");
                ticketExist = true;
            }
            message.append(" 여석이 생겼습니다!");

            if (ticketExist) {
                String phoneNumber = user.getUserPhoneNumber();
                snsManager.sendSnsForBookMark(message.toString(), phoneNumber, bookMark);
                bookMark.messageIsSent();
                log.info(message.toString());
            }
        }
    }


    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KorailSearchResponse {
        @JsonProperty("strResult")
        private String resultStatus; // SUCC or FAIL

        @JsonProperty("h_msg_txt")
        private String errorMessage;

        @JsonProperty("h_msg_cd")
        private String errorCode;

        @JsonProperty("trn_infos")
        private TrainInfoListWrapper trainInfos;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainInfoListWrapper {
        @JsonProperty("trn_info")
        private List<TrainInfo> trainInfoList;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrainInfo {
        @JsonProperty("h_trn_clsf_nm")
        private String trainTypeName; // KTX, 새마을호 등

        @JsonProperty("h_trn_no")
        private String trainNo;

        @JsonProperty("h_dpt_rs_stn_nm")
        private String depStation;

        @JsonProperty("h_dpt_tm")
        private String depTime; // HHmmss

        @JsonProperty("h_arv_rs_stn_nm")
        private String arrStation;

        @JsonProperty("h_arv_tm")
        private String arrTime; // HHmmss

        @JsonProperty("h_run_dt")
        private String runDate; // yyyyMMdd

        @JsonProperty("h_rsv_psb_flg")
        private String reservePossibleFlag; // Y or N

        @JsonProperty("h_spe_rsv_cd") // 특실
        private String specialSeatCode; // 11: 가능, 13: 매진, 00: 특실이 없음

        @JsonProperty("h_gen_rsv_cd") // 일반실
        private String generalSeatCode; // 11: 가능, 13: 매진

        @JsonProperty("h_stn_sale_flg") // 입석 + 좌석
        private String standingSeatFlag; // Y or N

        @JsonProperty("h_wait_rsv_flg") // 예약 대기
        private String waitReserveFlag; // 9: 예약 대기 가능, 0: 예약 대기 불가능, -2: 좌석 있음

        public boolean hasSpecialSeat() {
            return "11".equals(this.specialSeatCode);
        }

        public boolean hasGeneralSeat() {
            return "11".equals(this.generalSeatCode);
        }

        public boolean isStandingAvailable() {
            return "Y".equals(this.standingSeatFlag);
        }

        public boolean hasWaitingList() {
            return "9".equals(this.waitReserveFlag);
        }
    }
}