package com.zarinatta.zarinattacrawler.config;

import com.zarinatta.zarinattacrawler.enums.MainStation;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(2000);
        connManager.setDefaultMaxPerRoute(20);
        return connManager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connManager) {
        return HttpClients.custom()
                .setConnectionManager(connManager)
                .build();
    }

    @Bean
    public List<HttpPost> httpPostRequests(CloseableHttpClient httpClient) {
        List<HttpPost> requests = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter year = DateTimeFormatter.ofPattern("YYYY");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("EE", Locale.KOREAN);
        DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");

        for (MainStation depart : MainStation.values()) {
            for (MainStation arrive : MainStation.values()) {
                String payload ="txtGoStartCode=&txtGoEndCode=&radJobId=1&selGoTrain=05&txtSeatAttCd_4=015&txtSeatAttCd_3=000&txtSeatAttCd_2=000&txtPsgFlg_2=0&txtPsgFlg_3=0&txtPsgFlg_4=0&txtPsgFlg_5=0&chkCpn=N&selGoSeat1=015&selGoSeat2=&txtPsgCnt1=1&txtPsgCnt2=0&txtGoPage=1" +
                        "&txtGoAbrdDt="+today.format(total)+"&selGoRoom=&useSeatFlg=&useServiceFlg=&checkStnNm=Y&txtMenuId=11&SeandYo=N&txtGoStartCode2=&txtGoEndCode2=&hidEasyTalk=" +
                        "&txtGoStart="+URLEncoder.encode(depart.getName(), StandardCharsets.UTF_8)+
                        "&txtGoEnd="+URLEncoder.encode(arrive.getName(), StandardCharsets.UTF_8)+
                        "&start=2024.7.30&selGoHour=00&txtGoHour=000000&selGoYear="+today.format(year)+
                        "&selGoMonth="+today.format(month)+
                        "&selGoDay="+today.format(day)+
                        "&txtGoYoil="+URLEncoder.encode(today.format(week), StandardCharsets.UTF_8)+"" +
                        "&txtPsgFlg_1=1";
                HttpPost post = new HttpPost("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do");
                post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                post.setHeader("User-Agent",  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
                post.setEntity(new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED));
                requests.add(post);
            }
        }
        return requests;
    }
}
