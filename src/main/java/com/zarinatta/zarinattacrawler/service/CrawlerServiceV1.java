package com.zarinatta.zarinattacrawler.service;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.MainStation;
import com.zarinatta.zarinattacrawler.enums.Station;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//todo Spring Batch 해야 할듯

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrawlerServiceV1 {

    private final TicketRepository ticketRepository;

    /**
     * 매일 새벽 4시에 코레일 홈페이지를 크롤링
     */
    @Transactional
    public void crawlerData_OnceADay() {
        long startTime = System.currentTimeMillis();
        LocalDate today = LocalDate.now();
        DateTimeFormatter year = DateTimeFormatter.ofPattern("YYYY");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("EE", Locale.KOREAN);
        DateTimeFormatter total = DateTimeFormatter.ofPattern("YYYYMMDD");

        final String url = "https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do";
        Map<String, String> payload = new HashMap<>();
        payload.put("selGoTrain", "05");
        payload.put("txtPsgFlg_1", "1");
        payload.put("txtPsgFlg_2", "0");
        payload.put("txtPsgFlg_8", "0");
        payload.put("txtPsgFlg_3", "0");
        payload.put("txtPsgFlg_4", "0");
        payload.put("txtPsgFlg_5", "0");
        payload.put("txtSeatAttCd_3", "000");
        payload.put("txtSeatAttCd_2", "000");
        payload.put("txtSeatAttCd_4", "015");
        payload.put("selGoTrainRa", "05");
        payload.put("radJobId", "1");
        payload.put("selGoSeat1", "015");
        payload.put("txtPsgCnt1", "1");
        payload.put("txtPsgCnt2", "0");
        payload.put("txtGoPage", "1");
        payload.put("checkStnNm", "Y");
        payload.put("SeandYo", "N");
        payload.put("chkInitFlg", "Y");
        payload.put("txtMenuId", "11");
        payload.put("ra", "1");
        payload.put("strChkCpn", "N");
        payload.put("txtSrcarCnt", "0");
        payload.put("txtSrcarCnt1", "0");
        payload.put("hidRsvTpCd", "03");
        payload.put("txtPsgTpCd1", "1");
        payload.put("txtPsgTpCd2", "3");
        payload.put("txtPsgTpCd3", "1");
        payload.put("txtPsgTpCd5", "1");
        payload.put("txtPsgTpCd7", "1");
        payload.put("txtPsgTpCd8", "3");
        payload.put("txtDiscKndCd1", "000");
        payload.put("txtDiscKndCd2", "000");
        payload.put("txtDiscKndCd3", "111");
        payload.put("txtDiscKndCd5", "131");
        payload.put("txtDiscKndCd7", "112");
        payload.put("txtDiscKndCd8", "321");
        payload.put("txtCompaCnt1", "0");
        payload.put("txtCompaCnt2", "0");
        payload.put("txtCompaCnt3", "0");
        payload.put("txtCompaCnt4", "0");
        payload.put("txtCompaCnt5", "0");
        payload.put("txtCompaCnt6", "0");
        payload.put("txtCompaCnt7", "0");
        payload.put("txtCompaCnt8", "0");

        /**
         * 바뀌는 부분
         */
        payload.put("selGoYear", today.format(year));
        payload.put("selGoMonth", (today.format(month)));
        payload.put("selGoDay", today.format(day));
        payload.put("selGoHour", "00");
        payload.put("txtGoHour", "000000");
        payload.put("txtGoYoil", today.format(week));
        payload.put("txtGoAbrdDt", today.format(total));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        headers.put("Content-Language", "ko-KR");

        for(MainStation depart : MainStation.values()){
            for(MainStation arrive : MainStation.values()){
                payload.put("txtGoStart", depart.getName());
                payload.put("txtGoEnd", arrive.getName());
                Connection.Response response = null;
                try {
                    response = Jsoup.connect(url)
                            .headers(headers)
                            .data(payload)
                            .method(Connection.Method.POST)
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Document document = Jsoup.parse(new String(response.bodyAsBytes(), StandardCharsets.UTF_8));
                Elements table = document.select("tr");
                for (Element ticket : table) {
                    Elements rows = ticket.select("td");
                    List<String> ticketInfo = rows.eachText();
                    List<String> ticketSeat = ticket.select("td img").eachAttr("alt");
                    String ticketPrice = (rows.select("td > div > strong").text());
                    if (ticketInfo.size() > 4 && !ticketInfo.get(1).startsWith("SRT")){
                        ticketRepository.save(Ticket.builder()
                                        .ticketType(ticketInfo.get(1))
                                        .arriveDate(ticketInfo.get(2))
                                        .arriveTime(ticketInfo.get(2))
                                        .arriveStation(ticketInfo.get(2))
                                        .departTime(ticketInfo.get(3))
                                        .departStation(ticketInfo.get(3))
                                        .price(ticketPrice)
                                        .soldOut(ticketSeat.get(0).equals("좌석매진")).build());
                    }
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime/1000.0 + " seconds");
    }
}
