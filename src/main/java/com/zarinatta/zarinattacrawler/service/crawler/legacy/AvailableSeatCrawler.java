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
import java.time.LocalTime;
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
public class AvailableSeatCrawler {

    private final TicketRepository ticketRepository;
    private final CloseableHttpClient httpClient;
    private final List<HttpPost> httpPostRequests;
    private final int CONNECTION_COUNT = 1764; //42 * 42
    private List<Ticket> ticketList;

    public void getTicketList() {
        ticketList = ticketRepository.findAll();
        realtimeSeatCrawler();
    }

    /**
     * 실시간으로 여석을 크롤링
     */
    @Transactional
    public void realtimeSeatCrawler() {
        long startTime = System.currentTimeMillis();
        LocalDateTime now = LocalDateTime.now();

        for (int index = 0; index < CONNECTION_COUNT; index++) {
            HttpPost post = httpPostRequests.get(index);

            for (MainStationCode depart : MainStationCode.values()) {
                for (MainStationCode arrive : MainStationCode.values()) {
                    if(depart.equals(arrive)) break;
                    LocalDateTime crawlingTime = now;
                    while (!crawlingTime.toLocalDate().isAfter(now.toLocalDate().plusDays(7))) {
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
                        post.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));

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
                                String firstclass = ticket.select("td:nth-of-type(5) img").attr("alt");
                                String normalSeat = ticket.select("td:nth-of-type(6) img").attr("alt");
                                String babySeat = ticket.select("td:nth-of-type(7) img").attr("alt");
                                if (ticketInfo.size() > 4 && !ticketInfo.get(1).startsWith("SRT")) {
                                    //Ticket ticketDB = findTicket(ticketInfo.get(1), ticketInfo.get(2));
                                }
                            }
                            // 다음 쪽으로 이동
                            String next = String.valueOf(document.select("#divResult > table.btn > tbody > tr > td > a"));
                            System.out.println(next);
                            String nextDay = String.valueOf(document.select("#btn_next"));
                            System.out.println(nextDay);
                            Pattern pattern = Pattern.compile("btnNextReserve\\('([0-9]+)', '([0-9]+)'\\)");
                            Matcher nextMatcher = pattern.matcher(next);
                            Matcher nextDayMatcher = pattern.matcher(nextDay);

                            if (nextMatcher.find() && nextDayMatcher.find() ) {
                                crawlingTime = crawlingTime.plusDays(1);
                                LocalTime newTime = LocalTime.of(00, 00, 00);
                                crawlingTime = crawlingTime.withHour(newTime.getHour())
                                        .withMinute(newTime.getMinute())
                                        .withSecond(newTime.getSecond());
                            }
                            else if (nextMatcher.find() && !nextDayMatcher.find()) {
                                String date = nextMatcher.group(1);
                                String time = nextMatcher.group(2);
                                System.out.println("Date: " + date);
                                System.out.println("Time: " + time);
                                LocalTime newTime = LocalTime.of(Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(2, 3)), 00);
                                crawlingTime =
                                        crawlingTime.withHour(newTime.getHour())
                                        .withMinute(newTime.getMinute())
                                        .withSecond(newTime.getSecond());
                            }
                            else if (!nextMatcher.find() && !nextDayMatcher.find()) {
                                crawlingTime = crawlingTime.plusDays(1);
                                LocalTime newTime = LocalTime.of(00, 00, 00);
                                crawlingTime = crawlingTime.withHour(newTime.getHour())
                                        .withMinute(newTime.getMinute())
                                        .withSecond(newTime.getSecond());;
                            }
                            System.out.println(crawlingTime);
                            /*
                             //todo <a href="javascript:btnNextReserve('20240803', '145800');"><img src="/docs/2007/img/common/btn_next.gif" alt="다음"></a>
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
*/
                        } catch (IOException e) {throw new RuntimeException(e);}

                    }
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("총 걸린 시간 : " + estimatedTime / 1000.0 + " seconds");
    }


    private HttpPost changePostPayload(HttpPost httpPost, MainStationCode depart, MainStationCode arrive, LocalDateTime crawlingTime) {
        HttpPost post = new HttpPost("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        post.setHeader("Connection:", "keep-alive");
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
        return post;
    }

    private Ticket findTicket(String trainNo, String departTime){
        for (Ticket ticket : ticketList) {
            String departTimeDB = String.format("%s:%s", ticket.getDepartTime().substring(8, 10), ticket.getDepartTime().substring(10, 12));
            String departTimeCrawl = departTime.substring(departTime.length() - 5);
            if (ticket.getTicketType().contains(trainNo) && departTimeCrawl.equals(departTimeDB)) {
                return ticket;
            }
        }
        throw new RuntimeException("Ticket not found"+trainNo+" "+departTime);
    }

    private LocalDateTime findNextTime(String a){
        return LocalDateTime.now();
    }
}
