package com.zarinatta.zarinattacrawler.service.crawler;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeSeatCrawlerV2 {
    private final CloseableHttpAsyncClient httpClient;
    private final BookMarkRepository bookMarkRepository;
    private final List<SimpleHttpRequest> httpPostRequests;
    private final Semaphore semaphore = new Semaphore(5);

    public void startCycle() {
        long startTime = System.currentTimeMillis();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        List<BookMark> bookMarkList = bookMarkRepository.findAllByAfterNowJoinAll(dateTimeFormatter.format(LocalDateTime.now()));
        Map<Ticket, List<BookMark>> ticketBookMarkMap = makeBookMarkUserMap(bookMarkList);
        realtimeSeatCrawler(ticketBookMarkMap);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    private Map<Ticket, List<BookMark>> makeBookMarkUserMap(List<BookMark> bookMarkList) {
        Map<Ticket, List<BookMark>> ticketBookMarkMap = new HashMap<>();
        for (BookMark bookMark : bookMarkList) {
            ticketBookMarkMap
                    .computeIfAbsent(bookMark.getTicket(), k -> new ArrayList<>())
                    .add(bookMark);
        }
        return ticketBookMarkMap;
    }

    private void realtimeSeatCrawler(Map<Ticket, List<BookMark>> ticketBookMarkMap) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Map.Entry<Ticket, List<BookMark>> ticketBookMarkSet : ticketBookMarkMap.entrySet()) {
            Ticket target = ticketBookMarkSet.getKey();
            SimpleHttpRequest request = makeHttpBody(target.getDepartStation(), target.getArriveStation(), target.getDepartTime());
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    semaphore.acquire();
                    httpClient.execute(request, new FutureCallback<>() {
                        @Override
                        public void completed(SimpleHttpResponse response) {
                            try {
                                String responseBody = response.getBodyText();
                                Document document = Jsoup.parse(responseBody);
                                Elements table = document.select("tr");
                                Iterator<Element> tickets = table.iterator();
                                while (tickets.hasNext()) {
                                    Element ticket = tickets.next();
                                    Elements rows = ticket.select("td");
                                    List<String> ticketInfo = rows.eachText();
                                    if (isSameTicket(target, ticketInfo)) {
                                        List<BookMark> bookMarks = ticketBookMarkSet.getValue();
                                        System.out.println("hyeok");
                                        sendSMS(bookMarks, ticket);
                                    }
                                }
                            } finally {
                                semaphore.release();
                            }
                        }

                        @Override
                        public void failed(Exception ex) {
                            System.out.println("failed");
                            semaphore.release();
                        }

                        @Override
                        public void cancelled() {
                            System.out.println("cancelled");
                            semaphore.release();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private SimpleHttpRequest makeHttpBody(StationCode depart, StationCode arrive, String targetTime) {
        String payload = "txtGoStartCode=&txtGoEndCode=&radJobId=1&selGoTrain=05&txtSeatAttCd_4=015&txtSeatAttCd_3=000&txtSeatAttCd_2=000&txtPsgFlg_2=0&txtPsgFlg_3=0&txtPsgFlg_4=0&txtPsgFlg_5=0&chkCpn=N&selGoSeat1=015&selGoSeat2=&txtPsgCnt1=1&txtPsgCnt2=0&txtGoPage=1" +
                "&txtGoAbrdDt=" + targetTime.substring(0, 8) + "&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                "&txtGoStart=" + URLEncoder.encode(depart.name(), StandardCharsets.UTF_8) +
                "&txtGoEnd=" + URLEncoder.encode(arrive.name(), StandardCharsets.UTF_8) +
                "&selGoYear=" + targetTime.substring(0, 4) +
                "&selGoMonth=" + targetTime.substring(4, 6) +
                "&selGoDay=" + targetTime.substring(6, 8) +
                "&txtGoHour=" + minusTime(targetTime.substring(8, 10)) + targetTime.substring(10, 14) +
                "&txtPsgFlg_1=1";
        return SimpleRequestBuilder.post()
                .setUri("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do")
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .setBody(payload, ContentType.APPLICATION_FORM_URLENCODED)
                .build();
    }


    private HttpPost makeHttpBody(HttpPost httpPost, StationCode depart, StationCode arrive, String targetTime) {
        String payload = "txtGoStartCode=&txtGoEndCode=&radJobId=1&selGoTrain=05&txtSeatAttCd_4=015&txtSeatAttCd_3=000&txtSeatAttCd_2=000&txtPsgFlg_2=0&txtPsgFlg_3=0&txtPsgFlg_4=0&txtPsgFlg_5=0&chkCpn=N&selGoSeat1=015&selGoSeat2=&txtPsgCnt1=1&txtPsgCnt2=0&txtGoPage=1" +
                "&txtGoAbrdDt=" + targetTime.substring(0, 8) + "&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                "&txtGoStart=" + URLEncoder.encode(depart.name(), StandardCharsets.UTF_8) +
                "&txtGoEnd=" + URLEncoder.encode(arrive.name(), StandardCharsets.UTF_8) +
                "&selGoYear=" + targetTime.substring(0, 4) +
                "&selGoMonth=" + targetTime.substring(4, 6) +
                "&selGoDay=" + targetTime.substring(6, 8) +
                "&txtGoHour=" + minusTime(targetTime.substring(8, 10)) + targetTime.substring(10, 14) +
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
            String phoneNumber = bookMark.getUser().getPhoneNumber();
            System.out.println(phoneNumber);
            System.out.println(firstClass+" "+normalSeat+" "+babySeat+" "+waitSeat);
        }
    }
}
