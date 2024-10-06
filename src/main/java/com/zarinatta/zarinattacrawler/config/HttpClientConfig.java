package com.zarinatta.zarinattacrawler.config;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HttpClientConfig {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(20);
        return connManager;
    }

    @Bean
    public PoolingAsyncClientConnectionManager poolingAsyncClientConnectionManager(){
        PoolingAsyncClientConnectionManager connManager = new PoolingAsyncClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(20);
        return connManager;
    }


    @Bean
    public CloseableHttpClient httpClient() {
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
            BasicHeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator("Keep-Alive"));
            while (it.hasNext()) {
                HeaderElement he = it.next();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return TimeValue.ofSeconds(Long.parseLong(value));
                }
            }
            return TimeValue.ofSeconds(1200); // 기본 Keep-Alive 시간 설정
        };
        return HttpClients.custom()
                .setConnectionManager(null)
                .setKeepAliveStrategy(keepAliveStrategy)
                .build();
    }

    @Bean
    public CloseableHttpAsyncClient httpAsyncClient(PoolingAsyncClientConnectionManager connManager) {
        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(100))
                .build();
        closeableHttpAsyncClient.start();
        return closeableHttpAsyncClient;
    }

    @Bean
    public HttpPost httpPost() {
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
        return httpPost;
    }

    @Bean
    public List<HttpPost> httpPostRequests(CloseableHttpClient httpClient) {
        List<HttpPost> requests = new ArrayList<>();
        for(int i = 0; i< 200; i++){
            HttpPost post = new HttpPost("https://www.letskorail.com/ebizprd/EbizPrdTicketPr21111_i1.do");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
            post.setHeader("Connection:", "keep-alive");
            requests.add(post);
        }
        return requests;
    }

}
