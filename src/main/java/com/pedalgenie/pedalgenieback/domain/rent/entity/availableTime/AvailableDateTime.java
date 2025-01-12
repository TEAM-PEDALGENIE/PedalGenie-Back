package com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime;

import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableStatus.DELETED;
import static com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableStatus.USED;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class AvailableDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Enumerated(EnumType.STRING)
    private AvailableStatus rentStatus;

    public AvailableDateTime(final Product product,
                             final LocalDateTime localDateTime,
                             final AvailableStatus rentStatus){
        this.product=product;
        this.localDateTime = localDateTime;
        this.rentStatus=rentStatus;
    }
    public void changeStatus(final AvailableStatus status) {
        if (this.rentStatus.equals(DELETED)) {
            throw new CustomException(ErrorCode.CANT_UPDATE_DELETED);
        }
        this.rentStatus = status;
    }

    public boolean isPast() {
        final LocalDate nowDay = TimeMachine.dateOfNow();
        final LocalDate targetDay = localDateTime.toLocalDate();
        return targetDay.isBefore(nowDay);
    }

    public boolean isToday() {
        final LocalDate nowDay = TimeMachine.dateOfNow();
        final LocalDate targetDay = localDateTime.toLocalDate();
        return targetDay.isEqual(nowDay);
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
