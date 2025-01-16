package com.pedalgenie.pedalgenieback.domain.available.entity;

import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TimeSlot {
    public static final LocalTime MAX_TIME = LocalTime.of(23, 50);
    public static final int SLOT_INTERVAL_MINUTES = 30;


    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private TimeSlot(
            final LocalTime startTime,
            final LocalTime endTime
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        validateStartEndTime(this.startTime, this.endTime);

    }

    public static TimeSlot of(final LocalTime startTime, LocalTime endTime) {
        return new TimeSlot(startTime, endTime);
    }

    public static void validateStartEndTime(final LocalTime startTime, final LocalTime endTime) {
        if (endTime.isBefore(startTime) || startTime.equals(endTime)) {
            throw new CustomException(ErrorCode.IMPOSSIBLE_START_END_TIME);
        }
    }

    public static List<TimeSlot> generateTimeSlots(final LocalTime openTime, final LocalTime closeTime) {
        if (closeTime.isBefore(openTime)) {
            throw new CustomException(ErrorCode.IMPOSSIBLE_START_END_TIME);
        }

        List<TimeSlot> timeSlots = new ArrayList<>();
        LocalTime currentStart = openTime;

        while (!currentStart.plusMinutes(SLOT_INTERVAL_MINUTES).isAfter(closeTime)) {
            LocalTime currentEnd = currentStart.plusMinutes(SLOT_INTERVAL_MINUTES);
            timeSlots.add(TimeSlot.of(currentStart, currentEnd));
            currentStart = currentEnd;
        }

        return timeSlots;
    }


    public static List<TimeSlot> exceptTime(List<TimeSlot> timeSlots, LocalTime breakStartTime, LocalTime breakEndTime) {
        List<TimeSlot> validTimeSlots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {
            // 점심시간 범위에 해당하지 않는 타임슬롯만 validTimeSlots에 추가
            if (breakStartTime != null && breakEndTime != null) {
                if (timeSlot.getStartTime().isBefore(breakStartTime) || timeSlot.getStartTime().isAfter(breakEndTime)) {
                    validTimeSlots.add(timeSlot);
                }
            } else {
                validTimeSlots.add(timeSlot); // 예외시간이 없으면 모두 유효한 타임슬롯
            }
        }

        return validTimeSlots;
    }



}
