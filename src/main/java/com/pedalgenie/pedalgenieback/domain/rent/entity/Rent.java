package com.pedalgenie.pedalgenieback.domain.rent.entity;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

import static com.pedalgenie.pedalgenieback.domain.rent.entity.RentStatusType.주문_확인_중;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 변경
    @JoinColumn(name = "available_date_time_id", nullable = false)
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
        this(null, availableDateTime, rentStartTime, rentEndTime, product, member, 주문_확인_중);
    }


    public static Rent create(final AvailableDateTime availableDateTime,
                              final Product product,
                              final Member member,
                              final LocalDateTime rentEndTime) {

        // AvailableDateTime에서 날짜(localDate)와 시간(localTime)을 가져와 결합하여 rentStartTime 생성
        LocalDateTime rentStartTime = availableDateTime.getLocalDate().atTime(availableDateTime.getLocalTime());


        validateNextDay(rentStartTime,availableDateTime);
        validateRentEndDate(rentStartTime, rentEndTime);

        return new Rent(
                availableDateTime,
                rentStartTime,
                rentEndTime,
                product,
                member
        );

    }

    private static void validateNextDay(final LocalDateTime rentStartTime, final AvailableDateTime availableDateTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime minValidTime = currentTime.plusHours(48); // 최소 예약 가능한 시간 (현재 시간 + 48시간)

        // 48시간 이내로 예약을 완료하면 안 됨
        if (rentStartTime.isBefore(minValidTime)) {
            throw new CustomException(ErrorCode.INVALID_RENT_DATE);
        }
    }

    // 대여 종료 시간을 시작 시간에서 3일 이상, 30일 이하로 설정
    private static void validateRentEndDate(final LocalDateTime rentStartTime, final LocalDateTime rentEndDateTime) {

        // 대여 종료 날짜가 최소 3일 후인지 확인
        if (rentEndDateTime.isBefore(rentStartTime.plusDays(3))) {
            throw new CustomException(ErrorCode.INVALID_RENT_END_DATE_TOO_SOON);
        }

        // 대여 종료 날짜가 최대 30일을 초과하는지 확인
        if (rentEndDateTime.isAfter(rentStartTime.plusDays(30))) {
            throw new CustomException(ErrorCode.INVALID_RENT_END_DATE_TOO_LATE);
        }
    }

    // 이용 일자 상태 변경용
    public void changeAvailableTimeStatus(final AvailableStatus status){
        availableDateTime.changeStatus(status);
    }

    // 렌트 상태 픽업으로 변경
    public void updateToPickUp(){

        // 주문 확인 중 상태인지 확인
        if(!getRentStatusType().equals(RentStatusType.주문_확인_중)){
            throw new CustomException(ErrorCode.INVALID_ORDER_PENDING_STATE);
        }

        this.rentStatusType=RentStatusType.픽업_예정;
    }

    // 렌트 상태 사용 중으로 변경
    public void updateToRent(){

        // 픽업 예정 상태인지 확인
        if(!getRentStatusType().equals(RentStatusType.픽업_예정)){
            throw new CustomException(ErrorCode.INVALID_PICKUP_STATE);
        }

        this.rentStatusType=RentStatusType.사용_중;
    }

    // 렌트 상태 반납완료로 변경
    public void updateToRETURND(){

        // 사용 중 상태인지 확인
        if(!getRentStatusType().equals(RentStatusType.사용_중)){
            throw new CustomException(ErrorCode.INVALID_RENTED_STATE);
        }

        this.rentStatusType=RentStatusType.반납_완료;
        product.increaseStock(1); // 대여 가능 수량 증가

    }

    // 대여 취소 접수
    public void cancelRequested() {

        validateCancelRequest(); // 취소 가능 여부 검증
        this.rentStatusType = RentStatusType.취소_접수; // 취소 접수

        product.increaseStock(1); // 대여 가능 수량 증가
    }

    // 취소 가능 여부 검증
    private void validateCancelRequest() {
        LocalDateTime currentTime = LocalDateTime.now();

        // 대여 시작일 하루 전까지 취소 가능
        if (currentTime.isAfter(rentStartTime.minusDays(1))) {
            throw new CustomException(ErrorCode.RENT_CANCELLATION_DEADLINE_EXCEEDED);
        }

    }
    // 대여 취소 완료
    public void cancelCompleted(){

        // 이미 취소되었을 때
        if (this.rentStatusType == RentStatusType.취소_완료) {
            throw new CustomException(ErrorCode.RENT_ALREADY_CANCELED);
        }
        this.rentStatusType = RentStatusType.취소_완료;

    }


}
