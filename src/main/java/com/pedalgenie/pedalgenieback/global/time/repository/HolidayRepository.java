package com.pedalgenie.pedalgenieback.global.time.repository;

import com.pedalgenie.pedalgenieback.global.time.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByHolidayDate(LocalDate date);
}
