package com.pedalgenie.pedalgenieback.domain.rent.entity;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;

import static com.pedalgenie.pedalgenieback.domain.rent.entity.RentType.EDITABLE;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "interview_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "available_date_time_id", unique = true)
    private AvailableDateTime availableDateTime;

    @Column(nullable = false)
    private LocalDateTime rentStartTime;

    @Column(nullable = false)
    private LocalDateTime rentEndTime;

    @OneToOne
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    @Enumerated(EnumType.STRING)
    private RentType rentType;

    public Rent(final Long id,
                final AvailableDateTime availableDateTime,
                final LocalDateTime rentStartTime,
                final LocalDateTime rentEndTime,
                final Product product,
                final Member member,
                final RentType rentType){
        this.id=id;
        this.availableDateTime=availableDateTime;
        this.rentStartTime=rentStartTime;
        this.rentEndTime=rentEndTime;
        this.product=product;
        this.member=member;
        this.rentType=rentType;
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
                              final Member member,
                              final LocalDateTime rentEndDateTime){
        validateNextDay(availableDateTime);

        return new Rent(
                availableDateTime,
                availableDateTime.getLocalDateTime(),
                rentEndDateTime,
                product,
                member
        );

    }

    private static void validateNextDay(final AvailableDateTime availableDateTime) {
        if (availableDateTime.isPast() || availableDateTime.isToday()) {
            throw new CustomException(ErrorCode.INVALID_RENT_DATE);
        }
    }
}
