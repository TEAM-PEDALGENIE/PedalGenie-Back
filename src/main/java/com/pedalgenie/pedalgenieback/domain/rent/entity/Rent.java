package com.pedalgenie.pedalgenieback.domain.rent.entity;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableStatus;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

import static com.pedalgenie.pedalgenieback.domain.rent.entity.RentStatusType.EDITABLE;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "available_date_time_id", unique = true)
    private AvailableDateTime availableDateTime;

    @Column(nullable = false)
    private LocalDateTime rentStartTime;

    @Column(nullable = false)
    private LocalDateTime rentEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private RentStatusType rentStatusType;

    public Rent(final Long id,
                final AvailableDateTime availableDateTime,
                final LocalDateTime rentStartTime,
                final LocalDateTime rentEndTime,
                final Product product,
                final Member member,
                final RentStatusType rentType) {
        this.id = id;
        this.availableDateTime = availableDateTime;
        this.rentStartTime = rentStartTime;
        this.rentEndTime = rentEndTime;
        this.product = product;
        this.member = member;
        this.rentStatusType = rentType;
    }

    public Rent(final AvailableDateTime availableDateTime,
                final LocalDateTime rentStartTime,
                final LocalDateTime rentEndTime,
                final Product product,
                final Member member) {
        this(null, availableDateTime, rentStartTime, rentEndTime, product, member, EDITABLE);
    }


    public static Rent create(final AvailableDateTime availableDateTime,
                              final Product product,
                              final Member member) {

        // AvailableDateTime에서 시작 시간(localDateTime)을 가져옴
        LocalDateTime rentStartTime = availableDateTime.getLocalDateTime();

        // 대여 종료 시간을 시작 시간에서 3일 이상, 30일 이하로 설정
        LocalDateTime rentEndDateTime = calculateRentEndDate(rentStartTime);


        validateNextDay(availableDateTime);
        validateRentEndDate(rentEndDateTime);

        return new Rent(
                availableDateTime,
                rentStartTime,
                rentEndDateTime,
                product,
                member
        );

    }
    private static LocalDateTime calculateRentEndDate(LocalDateTime rentStartTime) {
        LocalDateTime rentEndDateTime = rentStartTime.plusDays(3); // 기본적으로 3일 후

        // 대여 종료 시간이 최대 30일을 초과하지 않도록 설정
        if (rentEndDateTime.isAfter(rentStartTime.plusDays(30))) {
            rentEndDateTime = rentStartTime.plusDays(30);
        }

        return rentEndDateTime;
    }

    // 오늘 또는 과거일 경우 대여 예약 불가
    private static void validateNextDay(final AvailableDateTime availableDateTime) {
        if (availableDateTime.isPast() || availableDateTime.isToday()) {
            throw new CustomException(ErrorCode.INVALID_RENT_DATE);
        }
    }

    private static void validateRentEndDate(final LocalDateTime rentEndDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 대여 종료 날짜가 최소 3일 후인지 확인
        if (rentEndDateTime.isBefore(currentDateTime.plusDays(3))) {
            throw new CustomException(ErrorCode.INVALID_RENT_END_DATE_TOO_SOON);
        }

        // 대여 종료 날짜가 최대 30일을 초과하는지 확인
        if (rentEndDateTime.isAfter(currentDateTime.plusDays(30))) {
            throw new CustomException(ErrorCode.INVALID_RENT_END_DATE_TOO_LATE);
        }
    }
    public void changeAvailableTimeStatus(final AvailableStatus status){
        availableDateTime.changeStatus(status);
    }
}
