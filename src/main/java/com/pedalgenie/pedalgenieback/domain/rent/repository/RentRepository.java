package com.pedalgenie.pedalgenieback.domain.rent.repository;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RentRepository extends JpaRepository<Rent, Long> {


    @Query("SELECT COUNT(r) FROM Rent r WHERE r.product.id = :productId AND r.rentStartTime = :startDate")
    long countByProductIdAndRentStartTime(@Param("productId") Long productId,
                                          @Param("startDate") LocalDateTime startDate);


}
