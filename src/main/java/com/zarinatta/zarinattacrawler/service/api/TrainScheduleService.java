package com.zarinatta.zarinattacrawler.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.TicketRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainScheduleService {

    private final TicketRepositoryCustom ticketRepository;
    private final String requestUrl = "http://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo";
    private final String serviceKey = "HfhAs61GSdPS9xgGhAlNLbH0YlnRdtbNa7MZVlJ6dAN5r7e3AYePUE9nQZv7X0PDqltq3o6ljr%2BKkLWb5TNzjg%3D%3D";
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    private final String ENCODE = "UTF-8";
    private final int OK = 200;
    private final int REDIRECT = 300;

    @Scheduled(cron = "0 0 02 * * *")
    public void getTrainSchedule() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
        executor.prestartAllCoreThreads();
        LocalDate weekAfter = LocalDate.now().plusDays(6);
        for (StationCode departureId : StationCode.values()) {
            for (StationCode arriveId : StationCode.values()) {
                executorService.submit(() -> {
                    try {
                        // URL 생성
                        URL url = buildUrl(departureId, arriveId, weekAfter);
                        // API 호출
                        StringBuilder sb = callApi(url);
                        // JSON 파싱 및 저장
                        convertToJsonAndSave(sb);
                    } catch (IOException e) {
                        log.error("API 호출 중 예외 발생 departure: {}  arrive: {}", departureId, arriveId);
                        log.error("API 호출 중 원본 예외", e);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        executorService.shutdown();
    }

    private URL buildUrl(StationCode departureId, StationCode arriveId, LocalDate weekAfter) throws UnsupportedEncodingException, MalformedURLException {
        DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");
        StringBuilder urlBuilder = new StringBuilder(requestUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", ENCODE));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", ENCODE));
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", ENCODE));
        urlBuilder.append("&" + URLEncoder.encode("depPlaceId", "UTF-8") + "=" + URLEncoder.encode(departureId.getCode(), ENCODE));
        urlBuilder.append("&" + URLEncoder.encode("arrPlaceId", "UTF-8") + "=" + URLEncoder.encode(arriveId.getCode(), ENCODE));
        urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(weekAfter.format(total), ENCODE));
        URL url = new URL(urlBuilder.toString());
        return url;
    }

    private StringBuilder callApi(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        // 호출 결과 파싱
        BufferedReader rd;
        int responseCode = conn.getResponseCode();
        if (responseCode >= OK && responseCode <= REDIRECT) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb;
    }

    public void convertToJsonAndSave(StringBuilder sb) {
        ObjectMapper mapper = new ObjectMapper();
        List<Ticket> ticketList = new ArrayList<>();
        try {
            JsonNode rootNode = mapper.readTree(sb.toString());
            JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    String depPlaceName = itemNode.path("depplacename").asText();
                    String arrPlaceName = itemNode.path("arrplacename").asText();
                    String depPlandTime = itemNode.path("depplandtime").asText();
                    String arrPlandTime = itemNode.path("arrplandtime").asText();
                    String trainGradeName = itemNode.path("traingradename").asText();
                    String trainNo = itemNode.path("trainno").asText();
                    int adultCharge = itemNode.path("adultcharge").asInt();
                    ticketList.add(Ticket.builder()
                            .ticketType(trainGradeName + " " + trainNo)
                            .departDate(depPlandTime.substring(0, 8))
                            .departStation(StationCode.valueOf(depPlaceName))
                            .departTime(depPlandTime)
                            .arriveStation(StationCode.valueOf(arrPlaceName))
                            .arriveTime(arrPlandTime)
                            .price(adultCharge + "원")
                            .build());
                }
            }
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 에러 발생", e);
            throw new RuntimeException(e);
        }
        ticketRepository.saveAll(ticketList);
    }
}
