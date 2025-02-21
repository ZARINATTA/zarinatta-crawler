package com.zarinatta.zarinattacrawler.service.crawler;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.entity.User;
import com.zarinatta.zarinattacrawler.enums.SeatLookingFor;
import com.zarinatta.zarinattacrawler.enums.SeatState;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import com.zarinatta.zarinattacrawler.sns.SnsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeSeatCrawler {

    private final CloseableHttpClient httpClient;
    private final BookMarkRepository bookMarkRepository;
    private final SnsManager snsManager;

    public void startCycle() {
        long startTime = System.currentTimeMillis();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        List<BookMark> bookMarkList = bookMarkRepository.findAllByAfterNow(dateFormatter.format(LocalDateTime.now()), timeFormatter.format(LocalDateTime.now()));
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
            HttpPost post = makeHttpPost(target.getDepartStation(), target.getArriveStation(), target.getDepartDate(), target.getDepartTime());
            try (CloseableHttpResponse response = httpClient.execute(post)) { // 데이터 가져 오기
                byte[] bytes = response.getEntity().getContent().readAllBytes();
                String responseBody = new String(bytes, StandardCharsets.UTF_8);
                Document document = Jsoup.parse(responseBody);
                response.getEntity().getContent().close();
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
            } finally {
                post.reset();
            }
        }
    }

    private HttpPost makeHttpPost(StationCode depart, StationCode arrive, String targetDate, String targetTime) {
        HttpPost httpPost = new HttpPost("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do");
        httpPost.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        httpPost.setHeader("accept-encoding", "gzip, deflate, br, zstd");
        httpPost.setHeader("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpPost.setHeader("cache-control", "max-age=0");
        httpPost.setHeader("connection", "keep-alive");
        httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("cookie", "WMONID=MXemLXO1KF-; _ga=GA1.1.1244795697.1717413303; pop_202404090001=done; JSESSIONID=gUjGo5vKviaUDFydiLATaZvDvWJOHQaHChLLWZwlEb1hhNawzXprhMYHsaCQ1jiF.kr005_servlet_engine4; _ga_LP2TSNTFG1=GS1.1.1722758826.53.1.1722762620.0.0.0");
        httpPost.setHeader("host", "www.letskorail.com");
        httpPost.setHeader("origin", "https://www.letskorail.com");
        httpPost.setHeader("sec-ch-ua", "\"Not)A;Brand\";v=\"99\", \"Google Chrome\";v=\"127\", \"Chromium\";v=\"127\"");
        httpPost.setHeader("sec-ch-ua-mobile", "?0");
        httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpPost.setHeader("sec-fetch-dest", "document");
        httpPost.setHeader("sec-fetch-mode", "navigate");
        httpPost.setHeader("sec-fetch-site", "same-origin");
        httpPost.setHeader("sec-fetch-user", "?1");
        httpPost.setHeader("upgrade-insecure-requests", "1");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");

        String payload = "txtGoStartCode=&txtGoEndCode=&radJobId=1&selGoTrain=05&txtSeatAttCd_4=015&txtSeatAttCd_3=000&txtSeatAttCd_2=000&txtPsgFlg_2=0&txtPsgFlg_3=0&txtPsgFlg_4=0&txtPsgFlg_5=0&chkCpn=N&selGoSeat1=015&selGoSeat2=&txtPsgCnt1=1&txtPsgCnt2=0&txtGoPage=1" +
                "&txtGoAbrdDt=" + targetDate + "&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                "&txtGoStart=" + URLEncoder.encode(depart.name(), StandardCharsets.UTF_8) +
                "&txtGoEnd=" + URLEncoder.encode(arrive.name(), StandardCharsets.UTF_8) +
                "&selGoYear=" + targetDate.substring(0, 4) +
                "&selGoMonth=" + targetDate.substring(4, 6) +
                "&selGoDay=" + targetDate.substring(6, 8) +
                "&txtGoHour=" + minusTime(targetTime.substring(0, 2)) + targetTime.substring(2, 4) + "00" +
                "&txtPsgFlg_1=1";
        httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
        System.out.println(payload);
        return httpPost;
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

    private String formatTime(String time) {
        String hour = time.substring(0, 2);
        String minute = time.substring(2, 4);
        return hour + ":" + minute;
    }

    private String minusTime(String time) {
        String minusTime = String.valueOf(Integer.parseInt(time) - 1);
        return String.format("%02d", Integer.parseInt(minusTime));
    }

    @Transactional
    public void sendSMS(List<BookMark> bookMarks, Element ticket) {
        String waitSeat = ticket.select("td:nth-of-type(10) img").attr("alt");
        String firstClass = ticket.select("td:nth-of-type(5) img").attr("alt");
        String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
        String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");

        for (BookMark bookMark : bookMarks) {
            User user = bookMark.getUser();
            Ticket myTicket = bookMark.getTicket();
            boolean ticketExist = false;
            StringBuilder message = new StringBuilder();
            message.append(user.getUserNick() + " 고객님께서 즐겨찾기 하신 ");
            message.append(myTicket.getDepartTime() + " 에 출발하는 " + myTicket.getTicketType() + " 열차의 ");
            if (bookMark.isWantWaitingReservation() && waitSeat.equals(SeatState.신청하기.name())) {
                message.append("예약 대기 / ");
                ticketExist = true;
            }
            if (bookMark.isWantFirstClass() && firstClass.equals(SeatState.예약하기.name())) {
                message.append("특실 / ");
                ticketExist = true;
            }
            if (bookMark.getWantNormalSeat().equals(SeatLookingFor.SEAT) && normalSeat.equals(SeatState.예약하기.name())) {
                message.append("일반실 좌석 / ");
                ticketExist = true;
            }
            if (bookMark.getWantNormalSeat().equals(SeatLookingFor.STANDING_SEAT) && (normalSeat.equals(SeatState.예약하기.name()) || normalSeat.equals(SeatState.입좌석묶음예약.name()))) {
                message.append("일반실 입좌석 / ");
                ticketExist = true;
            }
            if (bookMark.getWantBabySeat().equals(SeatLookingFor.SEAT) && babySeat.equals(SeatState.유아동반객실.name())) {
                message.append("유아실 좌석 / ");
                ticketExist = true;
            }
            if (bookMark.getWantBabySeat().equals(SeatLookingFor.STANDING_SEAT) && (babySeat.equals(SeatState.유아동반객실.name()) || babySeat.equals(SeatState.입좌석묶음예약.name()))) {
                message.append("유아실 입좌석 / ");
                ticketExist = true;
            }
            message.append("이 생겼습니다!");
            if (ticketExist) {
                String phoneNumber = user.getUserPhoneNumber();
                snsManager.sendSnsForBookMark(String.valueOf(message), phoneNumber, bookMark);
            }
            log.info(firstClass + " " + normalSeat + " " + babySeat + " " + waitSeat);
        }
    }
}
