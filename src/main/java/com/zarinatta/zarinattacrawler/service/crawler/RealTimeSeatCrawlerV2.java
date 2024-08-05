package com.zarinatta.zarinattacrawler.service.crawler;

import com.zarinatta.zarinattacrawler.entity.BookMark;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.entity.User;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.BookMarkRepository;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeSeatCrawlerV2 {

    private final CloseableHttpClient httpClient;
    private final BookMarkRepository bookMarkRepository;

    public void startCycle() {
        long startTime = System.currentTimeMillis();
        List<BookMark> bookMarkList = bookMarkRepository.findAllByTimeOutFalseJoinAll();
        Map<Ticket, List<BookMark>> ticketBookMarkMap = makeBookMarkUserMap(bookMarkList);
        realtimeSeatCrawler(ticketBookMarkMap);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    public Map<Ticket, List<BookMark>> makeBookMarkUserMap(List<BookMark> bookMarkList) {
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

    @Transactional
    public void realtimeSeatCrawler(Map<Ticket, List<BookMark>> ticketBookMarkMap) {
        for (Map.Entry<Ticket, List<BookMark>> ticketBookMarkSet : ticketBookMarkMap.entrySet() ) {
            Ticket target = ticketBookMarkSet.getKey();
            HttpPost post = makeHttpPost(target.getDepartStation(), target.getArriveStation(), target.getDepartTime());
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
                    if (isSameTicket(target, ticketInfo)){
                        String firstClass = ticket.select("td:nth-of-type(5) img").attr("alt");
                        String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
                        String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");
                        String waitSeat = ticket.select("td:nth-of-type(10) img").attr("alt");
                        System.out.println(target.getDepartTime());
                        System.out.println();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                post.reset();
            }

        }
    }

    private HttpPost makeHttpPost(StationCode depart, StationCode arrive, String crawlingTime) {
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
                "&txtGoAbrdDt=" + crawlingTime.substring(0, 8) + "&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                "&txtGoStart=" + URLEncoder.encode(depart.name(), StandardCharsets.UTF_8) +
                "&txtGoEnd=" + URLEncoder.encode(arrive.name(), StandardCharsets.UTF_8) +
                "&selGoYear=" + crawlingTime.substring(0, 4) +
                "&selGoMonth=" + crawlingTime.substring(4, 6) +
                "&selGoDay=" + crawlingTime.substring(6, 8) +
                "&txtGoHour=" + crawlingTime.substring(8, 14) +
                "&txtPsgFlg_1=1";
        httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
        System.out.println(payload);
        return httpPost;
    }

    private boolean isSameTicket(Ticket target, List<String> ticketInfo) {
        if (ticketInfo.size() > 4
                && !ticketInfo.get(1).startsWith("SRT")
                && target.getDepartStation().name().contains(ticketInfo.get(2))
                && target.getArriveStation().name().contains(ticketInfo.get(3))
                && formatTime(target.getDepartTime()).contains(ticketInfo.get(2))
                && formatTime(target.getArriveTime()).contains(ticketInfo.get(3))) {
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
}
