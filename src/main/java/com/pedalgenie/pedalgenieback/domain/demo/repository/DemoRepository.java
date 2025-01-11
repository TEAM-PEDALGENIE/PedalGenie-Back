package com.pedalgenie.pedalgenieback.domain.demo.repository;

import com.pedalgenie.pedalgenieback.domain.demo.entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface DemoRepository extends JpaRepository<Demo, Long> {
    List<Demo> findByMember_memberId(Long memberId);
}
