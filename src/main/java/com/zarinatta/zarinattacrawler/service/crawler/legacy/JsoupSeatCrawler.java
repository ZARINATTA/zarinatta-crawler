package com.zarinatta.zarinattacrawler.service.crawler.legacy;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.MainStationCode;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JsoupSeatCrawler {
    private final TicketRepository ticketRepository;
    private final CloseableHttpClient httpClient;
    private List<Ticket> ticketList;
    public void getTicketList() throws IOException {
        ticketList = ticketRepository.findAll();
        realtimeSeatCrawler();
    }

    @Transactional
    public void realtimeSeatCrawler() throws IOException {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        for (MainStationCode depart : MainStationCode.values()) {
            for (MainStationCode arrive : MainStationCode.values()) {
                if (depart.equals(arrive)) break;
                LocalDateTime crawlingTime = now;
                while (!crawlingTime.toLocalDate().isAfter(now.toLocalDate().plusDays(6))) {
                    Connection.Response response = Jsoup.connect("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do")
                                .headers(makeHttpHeader())
                                .data(makeHttpPayload(depart, arrive, crawlingTime))
                                .method(Connection.Method.POST)
                                .execute();


                        Document document = Jsoup.parse(new String(response.bodyAsBytes(), StandardCharsets.UTF_8));
                        Elements table = document.select("tr");
                        Iterator<Element> tickets = table.iterator();
                        while (tickets.hasNext()) {
                            Element ticket = tickets.next();
                            Elements rows = ticket.select("td");
                            List<String> ticketInfo = rows.eachText();
                            String firstClass = ticket.select("td:nth-of-type(5) img").attr("alt");
                            String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
                            String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");
                            if (ticketInfo.size() > 4 && !ticketInfo.get(1).startsWith("SRT")) {
                                ticketRepository.save(
                                        Ticket.builder()
                                                .ticketType(ticketInfo.get(1))
                                                .departDate(ticketInfo.get(2))
                                                .arriveTime(ticketInfo.get(2))
                                                .departTime(ticketInfo.get(3))
                                                .price(ticketInfo.get(4))
                                        .build());
                            }
                        }
                        // 다음 쪽으로 이동
                        String text = table.select("#divResult > table.btn > tbody > tr > td > a > img").attr("alt");
                        System.out.println(text);
                        if (text.equals("다음")) {
                            String time = table.select("tr:last-of-type td:nth-of-type(4)").html().split("<br>")[1].trim();
                            Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2})");
                            Matcher matcher = pattern.matcher(time);
                            if (matcher.find()) {
                                String hourStr = matcher.group(1);
                                String minuteStr = matcher.group(2);
                                // 문자열을 int로 변환
                                int hour = Integer.parseInt(hourStr);
                                int minute = Integer.parseInt(minuteStr);
                                LocalTime newTime = LocalTime.of(hour, minute, 00);
                                crawlingTime = crawlingTime.withHour(newTime.getHour())
                                        .withMinute(newTime.getMinute())
                                        .withSecond(newTime.getSecond());
                            }
                        } else {
                            crawlingTime = crawlingTime.plusDays(1);
                            LocalTime newTime = LocalTime.of(00, 00, 00);
                            crawlingTime = crawlingTime.withHour(newTime.getHour())
                                    .withMinute(newTime.getMinute())
                                    .withSecond(newTime.getSecond());
                        }
                    }

                }
            }


        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    private Map<String, String> makeHttpHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        headers.put("Connection", "keep-alive");
        return headers;
    }

    private Map<String, String> makeHttpPayload(MainStationCode depart, MainStationCode arrive, LocalDateTime crawlingTime){
        Map<String, String> payloadMap = new HashMap<>();

        DateTimeFormatter year = DateTimeFormatter.ofPattern("YYYY");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("EE", Locale.KOREAN);
        DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter txtGoHourFormat = DateTimeFormatter.ofPattern("HHmm");

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
        payloadMap.put("txtGoAbrdDt", crawlingTime.format(total));
        payloadMap.put("selGoRoom", "");
        payloadMap.put("useSeatFlg", "");
        payloadMap.put("useServiceFlg", "");
        payloadMap.put("checkStnNm", "Y");
        payloadMap.put("txtMenuId", "11");
        payloadMap.put("SeandYo", "N");
        payloadMap.put("txtGoStartCode2", "");
        payloadMap.put("txtGoEndCode2", "");
        payloadMap.put("hidEasyTalk", "");
        payloadMap.put("txtGoStart", depart.getName());
        payloadMap.put("txtGoEnd", arrive.getName());
        payloadMap.put("selGoYear", crawlingTime.format(year));
        payloadMap.put("selGoMonth", crawlingTime.format(month));
        payloadMap.put("selGoDay", crawlingTime.format(day));
        payloadMap.put("txtGoYoil",crawlingTime.format(week));
        payloadMap.put("txtGoHour", crawlingTime.format(txtGoHourFormat) + "00");
        payloadMap.put("txtPsgFlg_1", "1");

        return payloadMap;
    }
}
