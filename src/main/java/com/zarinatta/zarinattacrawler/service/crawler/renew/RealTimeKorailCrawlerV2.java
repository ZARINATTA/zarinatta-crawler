package com.zarinatta.zarinattacrawler.service.crawler.renew;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.entity.User;
import com.zarinatta.zarinattacrawler.enums.SeatLookingFor;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
import com.zarinatta.zarinattacrawler.sns.SnsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeKorailCrawlerV2 {

    private final CloseableHttpClient httpClient;
    private final TicketRepository ticketRepository;
    private final SnsManager snsManager;
    private final ObjectMapper objectMapper;

    /**
     * Map<Ticket, List<BookMark>>를 DB에서 한번에 가져오는 방식으로 변경
     */
    @Scheduled(fixedDelay = 30000)
    public void startCycle() {
        long startTime = System.currentTimeMillis();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        int page = 0;
        int chunkSize = 8;
        Slice<Long> ticketIds = ticketRepository.findTicketIdsAfterNow(
                dateFormatter.format(LocalDateTime.now()),
                timeFormatter.format(LocalDateTime.now()),
                PageRequest.of(page, chunkSize));
        while (ticketIds.hasContent()) {
            List<Ticket> targetTickets = ticketRepository.findTicketsWithDetailsByIds(ticketIds.getContent());
            realtimeSeatCrawler(targetTickets);
            long crawlTime = System.currentTimeMillis() - startTime;
            log.info("Chunk {} 크롤링 + DB 조회 소요 시간 : {} seconds", page, crawlTime / 1000.0);
            if (ticketIds.hasNext()) {
                page++;
                ticketIds = ticketRepository.findTicketIdsAfterNow(
                        dateFormatter.format(LocalDateTime.now()),
                        timeFormatter.format(LocalDateTime.now()),
                        PageRequest.of(page, chunkSize));
            } else {
                break;
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("전체 작업 소요 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    private void realtimeSeatCrawler(List<Ticket> ticketList) {
        for (Ticket target : ticketList) {
            HttpPost post = makeHttpPost(target.getDepartStation(), target.getArriveStation(), target.getDepartDate(), target.getDepartTime());
            try { // 데이터 가져 오기
                KorailResponseDto generalResponse = httpClient.execute(post, response -> {
                    HttpEntity entity = response.getEntity();
                    return objectMapper.readValue(entity.getContent(), KorailResponseDto.class);
                });
                if (generalResponse.getResultStatus().equals("SUCC")) {
                    TrainInfo realTimeTargetInfo = findTarget(target, generalResponse.getTrainInfos().getTrainInfoList());
                    sendSMS(target.getBookMarks(), realTimeTargetInfo);
                } else {
                    log.error("Korail Error Response: " + generalResponse.getMessageText());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                log.error(String.valueOf(e));
            }
            finally {
                post.reset();
                target.getBookMarks();
            }
            long minDelayMillis = 1000; // 1초
            long maxDelayMillis = 5000; // 5초
            long randomDelay = ThreadLocalRandom.current().nextLong(minDelayMillis, maxDelayMillis);
            try {
                Thread.sleep(randomDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private HttpPost makeHttpPost(StationCode depart, StationCode arrive, String targetDate, String targetTime) {
        HttpPost httpPost = new HttpPost("https://www.korail.com/classes/com.korail.mobile.seatMovie.ScheduleView");

        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");
        httpPost.setHeader("Origin", "https://www.korail.com");
        httpPost.setHeader("Referer", "https://www.korail.com/ticket/search/list");
        httpPost.setHeader("Accept", "application/json, text/plain, */*");
        httpPost.setHeader("Cookie", "WMONID=ZVHHEY2Cn2l; 40=Y; JSESSIONID=edkW4N5ZDH6eIo3kyleTzxkJTnkxnrKcJNV1tTPxesoiZ91LlcSmVZ5s0lAGoaYm.kr001_servlet_engine2; NF_KEY=488E75BF8F51FF9E4B1343E770B84EC97F64C5CDCC191B860FEEBC106431C6735D60E2837F83DB85773D309F0C50C898254AA46F8D3A83E1845A8CEF5996CD2154D66F1A95755416138A5165D3B254037E005BC0D785E3790733EB28CCFE6D9F22CD4CD31830FAFE71CB2EC16D36F774312C302C30");

        ContentType utf8ContentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("searchType", "GENERAL");
        builder.addTextBody("txtGoStart", depart.name(), utf8ContentType);
        builder.addTextBody("txtGoEnd", arrive.name(), utf8ContentType);
        builder.addTextBody("txtTrnGpCd", "109");
        builder.addTextBody("radJobId", "1");
        builder.addTextBody("adjStnScdlOfrFlg", "N");
        builder.addTextBody("srtCheckYn", "N");
        builder.addTextBody("ebizCrossCheck", "N");
        builder.addTextBody("txtPsgFlg_1", "1");
        builder.addTextBody("rtYn", "N");
        builder.addTextBody("txtGoAbrdDt", targetDate);
        builder.addTextBody("txtGoHour", targetTime + "00");
        builder.addTextBody("txtWkndUseFlg", "Y");
        builder.addTextBody("txtMenuId", "11");
        builder.addTextBody("txtSeatAttCd_4", "015"); // 일반석 : 15, 유아동반석 : 19, 휠체어석 : 21
        builder.addTextBody("tkTripChgQryFlg", "Y");
        builder.addTextBody("Device", "BH");
        builder.addTextBody("Version", "999999999");

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        return httpPost;
    }

    private TrainInfo findTarget(Ticket target, List<TrainInfo> trainInfoList) {
        for (TrainInfo trainInfo : trainInfoList) {
            if (trainInfo.getDepartureStation().equals(target.getDepartStation().name()) &&
                    trainInfo.getArrivalStation().equals(target.getArriveStation().name()) &&
                    trainInfo.getDepartureTime().substring(0, 4).equals(target.getDepartTime()) &&
                    trainInfo.getArrivalTime().substring(0, 4).equals(target.getArriveTime())) {
                return trainInfo;
            }
        }
        log.error("findTarget - 티켓을 찾을수 없습니다. (targetId : " + target.getId() + ")");
        return null;
    }

    public void sendSMS(List<BookMark> bookMarks, TrainInfo realTimeTargetInfo) {
        for (BookMark bookMark : bookMarks) {
            User user = bookMark.getUser();
            Ticket myTicket = bookMark.getTicket();
            boolean ticketExist = false;
            StringBuilder message = new StringBuilder();
            message.append("[자리나따] ");
            message.append(user.getUserNick() + "님, ");
            message.append(myTicket.getDepartTime().substring(0, 2) + ":" + myTicket.getDepartTime().substring(2) + " ");
            message.append(myTicket.getDepartStation() + " 출발 열차 ");
            if (bookMark.isWantWaitingReservation() && realTimeTargetInfo.isWaitAvailable()) {
                message.append("[예약대기]");
                ticketExist = true;
            }
            if (bookMark.isWantFirstClass() && realTimeTargetInfo.isSpecialAvailable()) {
                message.append("[특실]");
                ticketExist = true;
            }
            if (bookMark.getWantNormalSeat().equals(SeatLookingFor.SEAT) && realTimeTargetInfo.isGeneralAvailable()) {
                message.append("[일반실]");
                ticketExist = true;
            }
            if (bookMark.getWantNormalSeat().equals(SeatLookingFor.STANDING_SEAT) && (realTimeTargetInfo.isGeneralAvailable() || realTimeTargetInfo.isStandingAvailable())) {
                message.append("[일반실/입석]");
                ticketExist = true;
            }
            message.append(" 여석이 생겼습니다!");
            if (ticketExist) {
                String phoneNumber = user.getUserPhoneNumber();
                snsManager.sendSnsForBookMark(String.valueOf(message), phoneNumber, bookMark);
                bookMark.messageIsSent();
                log.info(message.toString());
            }
        }
    }
}