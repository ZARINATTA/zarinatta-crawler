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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainScheduleService {

    private final ApiService apiService;
    private final TicketRepositoryCustom ticketRepository;
    private final String requestUrl = "http://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo";
    private final String serviceKey = "HfhAs61GSdPS9xgGhAlNLbH0YlnRdtbNa7MZVlJ6dAN5r7e3AYePUE9nQZv7X0PDqltq3o6ljr%2BKkLWb5TNzjg%3D%3D";
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);
    private final String ENCODE = "UTF-8";

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void getTrainSchedule() {
        LocalDateTime startTime = LocalDateTime.now();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
        executor.prestartAllCoreThreads();
        LocalDate weekAfter = LocalDate.now().plusDays(6);
        log.info("========= {} 기차 시간표 배치 작업 시작=========", weekAfter);
        for (StationCode departureId : StationCode.values()) {
            for (StationCode arriveId : StationCode.values()) {
                executorService.submit(() -> {
                    try {
                        // URL 생성
                        URL url = buildUrl(departureId, arriveId, weekAfter);
                        // API 호출
                        StringBuilder sb = apiService.callTrainApi(url);
                        // JSON 파싱 및 저장
                        convertToJsonAndSave(sb);
                    } catch (IOException e) {
                        log.error("API 호출 중 예외 발생 departure: {}  arrive: {}", departureId, arriveId);
                        log.error("원본 예외 : ", e);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(12, TimeUnit.HOURS)) {
                log.warn("일정 시간 내에 모든 작업이 완료되지 않았습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("대기 중 인터럽트 발생", e);
        }
        LocalDateTime endTime = LocalDateTime.now();
        log.info("========= {} 기차 시간표 배치 작업 끝=========", weekAfter);
        log.info("소요시간 : {} minute =========", ChronoUnit.MINUTES.between(startTime, endTime));
    }

    private URL buildUrl(StationCode departureId, StationCode arriveId, LocalDate weekAfter) {
        DateTimeFormatter total = DateTimeFormatter.ofPattern("yyyyMMdd");
        StringBuilder urlBuilder = new StringBuilder(requestUrl);
        try {
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", ENCODE));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1000", ENCODE));
            urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", ENCODE));
            urlBuilder.append("&" + URLEncoder.encode("depPlaceId", "UTF-8") + "=" + URLEncoder.encode(departureId.getCode(), ENCODE));
            urlBuilder.append("&" + URLEncoder.encode("arrPlaceId", "UTF-8") + "=" + URLEncoder.encode(arriveId.getCode(), ENCODE));
            urlBuilder.append("&" + URLEncoder.encode("depPlandTime", "UTF-8") + "=" + URLEncoder.encode(weekAfter.format(total), ENCODE));
            URL url = new URL(urlBuilder.toString());
            return url;
        } catch (UnsupportedEncodingException e) {
            log.error("URL 인코딩 중 에러 발생", e);
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            log.error("URL 생성 중 에러 발생", e);
            throw new RuntimeException(e);
        }
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
                            .departTime(depPlandTime.substring(8, 12))
                            .arriveStation(StationCode.valueOf(arrPlaceName))
                            .arriveTime(arrPlandTime.substring(8, 12))
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
