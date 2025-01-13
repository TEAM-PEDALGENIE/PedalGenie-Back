package com.pedalgenie.pedalgenieback.domain.rent.repository;

import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvailableTimeRepository extends JpaRepository<AvailableDateTime, Long> {


    // 특정 상품의 특정 시간대에 해당하는 예약 가능 상태 조회
    Optional<AvailableDateTime> findByProductIdAndLocalDateTime(Long productId, LocalDateTime localDateTime);


}
