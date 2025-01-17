package com.pedalgenie.pedalgenieback.domain.available.entity;

import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus.DELETED;
import static com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus.USED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private LocalDate localDate;  // 날짜만 저장

    @Column(nullable = false)
    private LocalTime localTime;  // 시간만 저장

    @Enumerated(EnumType.STRING)
    private AvailableStatus rentStatus;

    public AvailableDateTime(final Long productId,
                             final LocalDate localDate,
                             final LocalTime localTime,
                             final AvailableStatus rentStatus) {
        this.productId = productId;
        this.localDate = localDate;
        this.localTime = localTime;
        this.rentStatus = rentStatus;
    }


    public void changeStatus(final AvailableStatus status) {
        if (this.rentStatus.equals(DELETED)) {
            throw new CustomException(ErrorCode.CANT_UPDATE_DELETED);
        }
        this.rentStatus = status;
    }

    public boolean isPast() {
        final LocalDate nowDay = TimeMachine.dateOfNow();
        return localDate.isBefore(nowDay);
    }

    public boolean isToday() {
        final LocalDate nowDay = TimeMachine.dateOfNow();
        return localDate.isEqual(nowDay);
    }

    public boolean isUsed() {
        return rentStatus.equals(USED);
    }

    public boolean isDeleted() {
        return rentStatus.equals(DELETED);
    }

    public boolean isSame(final Long id) {
        return this.id.equals(id);
    }
}