package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {

    private Long id;
    private AvailableDateTime availableDateTime;

    public static CalendarResponse from(final Rent rent){
        return CalendarResponse.builder()
                .id(rent.getId())
                .availableDateTime(rent.getAvailableDateTime())
                .build();
    }
}
