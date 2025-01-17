package com.pedalgenie.pedalgenieback.domain.available.repository;

import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailableTimeRepository extends JpaRepository<AvailableDateTime, Long> {


    List<AvailableDateTime> findByProductId(Long productId);

    List<AvailableDateTime> findByRentStatus(AvailableStatus rentStatus);

    // 특정 상품과 날짜에 대한 예약 가능한 시간 조회
    @Query("SELECT a FROM AvailableDateTime a WHERE a.productId = :productId AND a.localDate = :targetDate AND a.rentStatus != :status")
    List<AvailableDateTime> findByProductIdAndLocalDateAndStatus(@Param("productId") Long productId,
                                                        @Param("targetDate") LocalDate targetDate,
                                                        @Param("status") AvailableStatus status);

    // 특정 상품과 날짜에 대한 예약 가능한 시간 조회
    @Query("SELECT a FROM AvailableDateTime a WHERE a.productId = :productId AND a.localDate = :targetDate")
    List<AvailableDateTime> findByProductIdAndLocalDate(@Param("productId") Long productId,
                                                        @Param("targetDate") LocalDate targetDate);




}