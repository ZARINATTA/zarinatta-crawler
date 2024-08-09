package com.zarinatta.zarinattacrawler.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarinatta.zarinattacrawler.entity.Ticket;
import com.zarinatta.zarinattacrawler.enums.StationCode;
import com.zarinatta.zarinattacrawler.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InitialDataSetting implements CommandLineRunner {

    private final TicketRepository ticketRepository;

    private final String requestUrl = "http://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo";
    private final String serviceKey = "HfhAs61GSdPS9xgGhAlNLbH0YlnRdtbNa7MZVlJ6dAN5r7e3AYePUE9nQZv7X0PDqltq3o6ljr%2BKkLWb5TNzjg%3D%3D";
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public void run(String... args) throws Exception {
        log.info("초기 데이터 세팅 시작");
        //initialDataSet();
    }

    @Transactional
    public void initialDataSet() {
        List<Future<Void>> futures = new ArrayList<>();
        for (LocalDate date = LocalDate.now(); LocalDate.now().plusDays(7).isAfter(date); date = date.plusDays(1)) {
            for (StationCode depPlaceId : StationCode.values()) {
                for (StationCode arrPlaceId : StationCode.values()) {
                    LocalDate requestDate = date;
                    Future<Void> future = executorService.submit(() -> {
                        try {
                            // URL 생성
                            DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");
                            StringBuilder urlBuilder = new StringBuilder(requestUrl);
                            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
                            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
                            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8"));
                            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
                            urlBuilder.append("&" + URLEncoder.encode("depPlaceId", "UTF-8") + "=" + URLEncoder.encode(depPlaceId.getCode(), "UTF-8"));
                            urlBuilder.append("&" + URLEncoder.encode("arrPlaceId", "UTF-8") + "=" + URLEncoder.encode(arrPlaceId.getCode(), "UTF-8"));
                            urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(requestDate.format(total), "UTF-8"));
                            URL url = new URL(urlBuilder.toString());

                            // API 호출
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Content-type", "application/json");

                            // 호출 결과 파싱
                            BufferedReader rd;
                            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
                            convertToJsonAndSave(sb);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    });
                    futures.add(future);
                }
            }
        }
        // 모든 작업이 완료될 때까지 대기
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("InitialDataSetting 에서 future 처리 중 오류 발생", e);
            }
        }
        executorService.shutdown();
    }

    @Transactional
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
            throw new RuntimeException(e);
        }
        ticketRepository.saveAll(ticketList);
    }


}
