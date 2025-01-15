package com.pedalgenie.pedalgenieback.domain.rent.dto.response;


import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private List<CalendarResponse> calendar;

    public static ScheduleResponse from(final List<Rent> rents){
        return new ScheduleResponse(rents.stream()
                .map(CalendarResponse::from)
                .toList());
    }
}
