package com.pedalgenie.pedalgenieback.domain.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DemoSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long demoSlotId;

    private Long shopId;
    private LocalDate demoDate;
    private LocalTime timeSlot;

    private int bookedQuantity;
    private boolean isAvailable;

    @Builder
    public DemoSlot(Long demoSlotId, Long shopId, LocalDate demoDate, LocalTime timeSlot, int bookedQuantity, boolean isAvailable) {
        this.demoSlotId = demoSlotId;
        this.shopId = shopId;
        this.demoDate = demoDate;
        this.timeSlot = timeSlot;
        this.bookedQuantity = bookedQuantity;
        this.isAvailable = isAvailable;
    }

    public void markAsBooked(int demoQuantityPerDay) {
        this.bookedQuantity += 1;

        // 최대 예약 가능 수에 도달하면 슬롯을 더 이상 예약 불가능 상태로 변경
        if (this.bookedQuantity >= demoQuantityPerDay) {
            this.isAvailable = false;
        }
    }

    public void decreaseBookedQuantity() {
        if (bookedQuantity > 0) {
            bookedQuantity -=1;
        }
    }
}
