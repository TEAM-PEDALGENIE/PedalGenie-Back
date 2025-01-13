package com.pedalgenie.pedalgenieback.global.time.application;

import com.pedalgenie.pedalgenieback.global.time.dto.HolidayDto;
import com.pedalgenie.pedalgenieback.global.time.dto.HolidayResponse;
import com.pedalgenie.pedalgenieback.global.time.entity.Holiday;
import com.pedalgenie.pedalgenieback.global.time.repository.HolidayRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HolidayService {
    private final HolidayRepository holidayRepository;

    @Value("${holiday-api.uri}")
    private String apiUrl;

    @Value("${holiday-api.key}")
    private String apiKey;


    // 서비스 실행 시 공휴일 데이터를 초기화하는 메서드
    @PostConstruct
    public void init() {
        int currentYear = LocalDate.now().getYear();
        int nextYear = currentYear + 1;

        // 현재 연도와 다음 연도의 공휴일이 존재하지 않으면 동기화
        if (!holidayRepository.existsByHolidayDate(LocalDate.of(currentYear, 1, 1)) &&
                !holidayRepository.existsByHolidayDate(LocalDate.of(nextYear, 1, 1))) {
            syncHolidaysForYear(currentYear);
            syncHolidaysForYear(nextYear);
        }
    }

    // 매년 12월 1일 자정에 공휴일 데이터 동기화
    @Scheduled(cron = "0 0 0 1 12 *")
    public void syncHolidaysForNextYear() {
        int nextYear = LocalDate.now().getYear() + 1;

        // 다음 연도의 공휴일이 존재하지 않으면 동기화
        if (!holidayRepository.existsByHolidayDate(LocalDate.of(nextYear, 1, 1))) {
            syncHolidaysForYear(nextYear);
        }
    }


    // 공휴일 API 요청하여 동기화하는 메서드
    @Transactional
    public void syncHolidaysForYear(int year) {
        String url = apiUrl
                + "?ServiceKey=" + apiKey
                + "&pageNo=1"
                + "&numOfRows=100"
                + "&solYear=" + year;

        try {
            // API 호출
            RestTemplate restTemplate = createRestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/xml;charset=UTF-8"));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<HolidayResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    HolidayResponse.class
            );

            HolidayResponse responseBody = response.getBody();

            if (responseBody != null && responseBody.getBody() != null && responseBody.getBody().getItems() != null) {
                List<HolidayDto> holidays = responseBody.getBody().getItems().getHolidays();

                // 기존 데이터 삭제하는거 빠져있음

                for (HolidayDto holiday : holidays) {
                    // 공휴일 데이터를 저장
                    LocalDate holidayDate = LocalDate.parse(holiday.getLocdate(), DateTimeFormatter.BASIC_ISO_DATE);
                    holidayRepository.save(
                            Holiday.builder()
                                    .holidayDate(holidayDate)
                                    .holidayName(holiday.getDateName())
                                    .build()
                    );
                }
                log.info("Successfully synced holidays for year {}", year);
            }
        } catch (Exception e) {
            log.error("Failed to sync holidays for year {}: {}", year, e.getMessage());
        }
    }

    private RestTemplate createRestTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        RestTemplate restTemplate = new RestTemplate(messageConverters);

        return restTemplate;
    }


    // 공휴일 여부 확인 메서드
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date);
    }

}

