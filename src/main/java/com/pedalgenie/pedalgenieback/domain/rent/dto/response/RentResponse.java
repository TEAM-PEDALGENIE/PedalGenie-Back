package com.pedalgenie.pedalgenieback.domain.rent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.entity.RentStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentResponse { // 예약 생성 이후 조회

    private Long rentId;
    private RentStatusType status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime rentStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime rentEndTime;

    public static RentResponse from(final Rent rent){
        return RentResponse.builder()
                .rentId(rent.getId())
                .status(rent.getRentStatusType())
                .rentStartTime(rent.getRentStartTime())
                .rentEndTime(rent.getRentEndTime())
                .build();
    }
}
