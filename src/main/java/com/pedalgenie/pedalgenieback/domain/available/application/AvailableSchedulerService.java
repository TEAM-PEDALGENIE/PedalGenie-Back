package com.pedalgenie.pedalgenieback.domain.available.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AvailableSchedulerService {

    private final AvailableTimeService availableTimeService;

//    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정 실행
    @Scheduled(cron = "0 58 22 * * *", zone = "Asia/Seoul") // 테스트
    public void resetAndGenerateAvailableTimes() {
        log.info("Scheduled task is running");
        availableTimeService.refreshAvailableTimes();

    }

}
