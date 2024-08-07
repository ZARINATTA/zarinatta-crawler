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
