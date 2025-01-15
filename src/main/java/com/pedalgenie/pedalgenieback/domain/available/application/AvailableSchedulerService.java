package com.pedalgenie.pedalgenieback.domain.available.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailableSchedulerService {

    private final AvailableTimeService availableTimeService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 자정 실행
//@Scheduled(cron = "0 41 15 * * *") // 3시 41분 테스트
    public void resetAndGenerateAvailableTimes() {
        availableTimeService.refreshAvailableTimes();

    }


}
