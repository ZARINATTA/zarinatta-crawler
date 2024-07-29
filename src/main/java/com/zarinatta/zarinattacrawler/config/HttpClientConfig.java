package com.zarinatta.zarinattacrawler.config;

import com.zarinatta.zarinattacrawler.enums.MainStation;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("selGoTrain", "05"));
                params.add(new BasicNameValuePair("txtPsgFlg_1", "1"));
                params.add(new BasicNameValuePair("txtPsgFlg_2", "0"));
                params.add(new BasicNameValuePair("txtPsgFlg_8", "0"));
                params.add(new BasicNameValuePair("txtPsgFlg_3", "0"));
                params.add(new BasicNameValuePair("txtPsgFlg_4", "0"));
                params.add(new BasicNameValuePair("txtPsgFlg_5", "0"));
                params.add(new BasicNameValuePair("txtSeatAttCd_3", "000"));
                params.add(new BasicNameValuePair("txtSeatAttCd_2", "000"));
                params.add(new BasicNameValuePair("txtSeatAttCd_4", "015"));
                params.add(new BasicNameValuePair("selGoTrainRa", "05"));
                params.add(new BasicNameValuePair("radJobId", "1"));
                params.add(new BasicNameValuePair("selGoSeat1", "015"));
                params.add(new BasicNameValuePair("txtPsgCnt1", "1"));
                params.add(new BasicNameValuePair("txtGoPage", "1"));
                params.add(new BasicNameValuePair("checkStnNm", "Y"));
                params.add(new BasicNameValuePair("SeandYo", "N"));
                params.add(new BasicNameValuePair("chkInitFlg", "Y"));
                params.add(new BasicNameValuePair("txtMenuId", "11"));
                params.add(new BasicNameValuePair("ra", "1"));
                params.add(new BasicNameValuePair("txtPsgTpCd1", "1"));
                params.add(new BasicNameValuePair("txtPsgTpCd2", "3"));
                params.add(new BasicNameValuePair("txtPsgTpCd3", "1"));
                params.add(new BasicNameValuePair("txtPsgTpCd5", "1"));
                params.add(new BasicNameValuePair("txtPsgTpCd7", "1"));
                params.add(new BasicNameValuePair("txtPsgTpCd8", "3"));
                params.add(new BasicNameValuePair("txtDiscKndCd1", "000"));
                params.add(new BasicNameValuePair("txtDiscKndCd2", "000"));
                params.add(new BasicNameValuePair("txtDiscKndCd3", "111"));
                params.add(new BasicNameValuePair("txtDiscKndCd5", "131"));
                params.add(new BasicNameValuePair("txtDiscKndCd7", "112"));
                params.add(new BasicNameValuePair("txtDiscKndCd8", "321"));
                params.add(new BasicNameValuePair("txtGoStart", URLEncoder.encode(depart.getName(), StandardCharsets.UTF_8)));
                params.add(new BasicNameValuePair("txtGoEnd", URLEncoder.encode(arrive.getName(), StandardCharsets.UTF_8)));
                params.add(new BasicNameValuePair("selGoYear", today.format(year)));
                params.add(new BasicNameValuePair("selGoMonth", today.format(month)));
                params.add(new BasicNameValuePair("selGoDay", today.format(day)));
                params.add(new BasicNameValuePair("txtGoAbrdDt", today.format(total)));
                params.add(new BasicNameValuePair("txtGoYoil", URLEncoder.encode(today.format(week), StandardCharsets.UTF_8)));
                params.add(new BasicNameValuePair("selGoHour", "19"));
                params.add(new BasicNameValuePair("txtGoHour", "192100"));


                HttpPost post = new HttpPost("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do");
                post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
                post.setEntity(new UrlEncodedFormEntity(params));
                requests.add(post);
            }
        }
        return requests;
    }
}
