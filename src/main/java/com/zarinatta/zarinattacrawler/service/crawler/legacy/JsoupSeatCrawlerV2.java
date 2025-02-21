package com.zarinatta.zarinattacrawler.service.crawler.legacy;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JsoupSeatCrawlerV2 {

    private final BookMarkRepository bookMarkRepository;

    public void startCycle() {
        long startTime = System.currentTimeMillis();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        List<BookMark> bookMarkList = bookMarkRepository.findAllByAfterNowJoinAll(dateTimeFormatter.format(LocalDateTime.now())); //todo time out을 기준으로 하기 보다, 현재 시간 기준으로 가쟈오기
        Map<Ticket, List<BookMark>> ticketBookMarkMap = makeBookMarkUserMap(bookMarkList);
        realtimeSeatCrawler(ticketBookMarkMap);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    private Map<Ticket, List<BookMark>> makeBookMarkUserMap(List<BookMark> bookMarkList) {
        Map<Ticket, List<BookMark>> ticketBookMarkMap = new HashMap<>();
        for (BookMark bookMark : bookMarkList) {
            if (ticketBookMarkMap.containsKey(bookMark.getTicket())) {
                ticketBookMarkMap.get(bookMark.getTicket()).add(bookMark);
            } else {
                List<BookMark> list = new ArrayList<>();
                list.add(bookMark);
                ticketBookMarkMap.put(bookMark.getTicket(), list);
            }
        }
        return ticketBookMarkMap;
    }

    private void realtimeSeatCrawler(Map<Ticket, List<BookMark>> ticketBookMarkMap) {
        for (Map.Entry<Ticket, List<BookMark>> ticketBookMarkSet : ticketBookMarkMap.entrySet()) {
            Ticket target = ticketBookMarkSet.getKey();
            try {
                Connection.Response response = Jsoup.connect("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do")
                        .headers(makeHttpHeader())
                        .data(makeHttpPayload(target.getDepartStation(), target.getArriveStation(), target.getDepartTime()))
                        .method(Connection.Method.POST)
                        .execute();
                Document document = Jsoup.parse(new String(response.bodyAsBytes(), StandardCharsets.UTF_8));
                Elements table = document.select("tr");
                Iterator<Element> tickets = table.iterator();
                while (tickets.hasNext()) {
                    Element ticket = tickets.next();
                    Elements rows = ticket.select("td");
                    List<String> ticketInfo = rows.eachText();
                    if (isSameTicket(target, ticketInfo)) {
                        List<BookMark> bookMarks = ticketBookMarkSet.getValue();
                        sendSMS(bookMarks, ticket);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, String> makeHttpHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        headers.put("Connection", "keep-alive");
        return headers;
    }

    private Map<String, String> makeHttpPayload(StationCode depart, StationCode arrive, String targetTime) {
        Map<String, String> payloadMap = new HashMap<>();

        payloadMap.put("txtGoStartCode", "");
        payloadMap.put("txtGoEndCode", "");
        payloadMap.put("radJobId", "1");
        payloadMap.put("selGoTrain", "05");
        payloadMap.put("txtSeatAttCd_4", "015");
        payloadMap.put("txtSeatAttCd_3", "000");
        payloadMap.put("txtSeatAttCd_2", "000");
        payloadMap.put("txtPsgFlg_2", "0");
        payloadMap.put("txtPsgFlg_3", "0");
        payloadMap.put("txtPsgFlg_4", "0");
        payloadMap.put("txtPsgFlg_5", "0");
        payloadMap.put("chkCpn", "N");
        payloadMap.put("selGoSeat1", "015");
        payloadMap.put("selGoSeat2", "");
        payloadMap.put("txtPsgCnt1", "1");
        payloadMap.put("txtPsgCnt2", "0");
        payloadMap.put("txtGoPage", "1");
        payloadMap.put("txtGoAbrdDt", targetTime.substring(0, 8));
        payloadMap.put("selGoRoom", "");
        payloadMap.put("useSeatFlg", "");
        payloadMap.put("useServiceFlg", "");
        payloadMap.put("checkStnNm", "Y");
        payloadMap.put("txtMenuId", "11");
        payloadMap.put("SeandYo", "N");
        payloadMap.put("txtGoStartCode2", "");
        payloadMap.put("txtGoEndCode2", "");
        payloadMap.put("hidEasyTalk", "");
        payloadMap.put("txtGoStart", depart.name());
        payloadMap.put("txtGoEnd", arrive.name());
        payloadMap.put("selGoYear", targetTime.substring(0, 4));
        payloadMap.put("selGoMonth", targetTime.substring(4, 6));
        payloadMap.put("selGoDay", targetTime.substring(6, 8));
        payloadMap.put("txtGoHour", minusTime(targetTime.substring(8, 10)) + targetTime.substring(10, 12) + "00");
        payloadMap.put("txtPsgFlg_1", "1");

        return payloadMap;
    }

    private boolean isSameTicket(Ticket target, List<String> ticketInfo) {
        String departInfo = target.getDepartStation().name() + " " + formatTime(target.getDepartTime());
        String arriveInfo = target.getArriveStation().name() + " " + formatTime(target.getArriveTime());
        if (ticketInfo.size() > 4
                && !ticketInfo.get(1).startsWith("SRT")
                && departInfo.equals(ticketInfo.get(2))
                && arriveInfo.equals(ticketInfo.get(3))) {
            return true;
        }
        return false;
    }

    private String formatTime(String dateTime) {
        String timePart = dateTime.substring(8, 12);
        String hour = timePart.substring(0, 2);
        String minute = timePart.substring(2, 4);
        return hour + ":" + minute;
    }

    private String minusTime(String time) {
        String minusTime = String.valueOf(Integer.parseInt(time) - 1);
        return String.format("%02d", Integer.parseInt(minusTime));
    }

    private void sendSMS(List<BookMark> bookMarks, Element ticket) {
        String waitSeat = ticket.select("td:nth-of-type(10) img").attr("alt");
        String firstClass = ticket.select("td:nth-of-type(5) img").attr("alt");
        String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
        String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");

        for (BookMark bookMark : bookMarks) {
            if (bookMark.isWantWaitingReservation() && waitSeat.equals("신청하기")) {
                System.out.println("1");
            }
            if (bookMark.isWantFirstClass() && firstClass.equals("예약하기")) {
                System.out.println("2");
            }
            if (bookMark.getWantNormalSeat().name().equals("SEAT") && normalSeat.equals("예약하기")) {
                System.out.println("3");
            }
            if (bookMark.getWantNormalSeat().name().equals("STANDING_SEAT") && (normalSeat.equals("예약하기") || normalSeat.equals("입좌석묶음예약"))) {
                System.out.println("4");
            }
            if (bookMark.getWantBabySeat().name().equals("SEAT") && babySeat.equals("유아동반객실")) {
                System.out.println("5");
            }
            if (bookMark.getWantBabySeat().name().equals("STANDING_SEAT") && (babySeat.equals("유아동반객실") || babySeat.equals("입좌석묶음예약"))) {
                System.out.println("6");
            }
            String phoneNumber = "bookMark.getUser().getPhoneNumber()";
            System.out.println(phoneNumber);
            System.out.println(firstClass+" "+normalSeat+" "+babySeat+" "+waitSeat);
        }
    }
}
