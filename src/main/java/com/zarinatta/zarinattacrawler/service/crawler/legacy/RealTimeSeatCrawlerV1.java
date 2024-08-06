package com.zarinatta.zarinattacrawler.service.crawler.legacy;

import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.MainStationCode;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeSeatCrawlerV1 {

    private final TicketRepository ticketRepository;
    private final CloseableHttpClient httpClient;
    private List<Ticket> ticketList;

    public void getTicketList() {
        ticketList = ticketRepository.findAll();
        realtimeSeatCrawler();
    }

    @Transactional
    public void realtimeSeatCrawler() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();
        for (MainStationCode depart : MainStationCode.values()) {
            for (MainStationCode arrive : MainStationCode.values()) {
                if (depart.equals(arrive)) break;
                LocalDateTime crawlingTime = now;
                while (!crawlingTime.toLocalDate().isAfter(now.toLocalDate().plusDays(6))) {
                    HttpPost post = makeHttpPost(depart, arrive, crawlingTime);
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
                            String firstClass = ticket.select("td:nth-of-type(5) img").attr("alt");
                            String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
                            String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");
                            if (ticketInfo.size() > 4 && !ticketInfo.get(1).startsWith("SRT")) {
                                //todo 임의로 만든 내용임
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
                        String nextPage = table.select("#divResult > table.btn > tbody > tr > td").html();
                        System.out.println(nextPage);
                        Pattern timeExtractor = Pattern.compile("btnNextReserve\\('([0-9]{8})', '([0-9]{6})'\\)");
                        Matcher timeMatcher = timeExtractor.matcher(nextPage);
                        if (timeMatcher.find()) {
                            String date = timeMatcher.group(1);
                            String time = timeMatcher.group(2);
                            System.out.println("date: " + date);
                            System.out.println("time: " + time);
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                            LocalDateTime newDateTime = LocalDateTime.parse(date + time, dateFormatter);
                            // crawlingTime을 newDateTime으로 갱신
                            crawlingTime = crawlingTime.withYear(newDateTime.getYear())
                                    .withMonth(newDateTime.getMonthValue())
                                    .withDayOfMonth(newDateTime.getDayOfMonth())
                                    .withHour(newDateTime.getHour())
                                    .withMinute(newDateTime.getMinute())
                                    .withSecond(newDateTime.getSecond());

                            // 결과 출력
                            System.out.println("Updated crawlingTime: " + crawlingTime);
                        } else {
                            System.out.println("No next page");
                            crawlingTime = crawlingTime.plusDays(7);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        post.reset();
                    }

                }
            }
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }

    public HttpPost makeHttpPost(MainStationCode depart, MainStationCode arrive, LocalDateTime crawlingTime) {
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

        DateTimeFormatter year = DateTimeFormatter.ofPattern("YYYY");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("EE", Locale.KOREAN);
        DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter txtGoHourFormat = DateTimeFormatter.ofPattern("HHmm");
        String payload = "txtGoStartCode=&txtGoEndCode=&radJobId=1&selGoTrain=05&txtSeatAttCd_4=015&txtSeatAttCd_3=000&txtSeatAttCd_2=000&txtPsgFlg_2=0&txtPsgFlg_3=0&txtPsgFlg_4=0&txtPsgFlg_5=0&chkCpn=N&selGoSeat1=015&selGoSeat2=&txtPsgCnt1=1&txtPsgCnt2=0&txtGoPage=1" +
                "&txtGoAbrdDt=" + crawlingTime.format(total) + "&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                "&txtGoStart=" + URLEncoder.encode(depart.getName(), StandardCharsets.UTF_8) +
                "&txtGoEnd=" + URLEncoder.encode(arrive.getName(), StandardCharsets.UTF_8) +
                "&selGoYear=" + crawlingTime.format(year) +
                "&selGoMonth=" + crawlingTime.format(month) +
                "&selGoDay=" + crawlingTime.format(day) +
                "&txtGoYoil=" + URLEncoder.encode(crawlingTime.format(week), StandardCharsets.UTF_8) +
                "&txtGoHour=" + crawlingTime.format(txtGoHourFormat) + "00" +
                "&txtPsgFlg_1=1";
        httpPost.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
        System.out.println(payload);
        return httpPost;
    }
}
