package com.pedalgenie.pedalgenieback.domain.demo.repository;

import com.pedalgenie.pedalgenieback.domain.demo.entity.Demo;
import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DemoRepository extends JpaRepository<Demo, Long> {
    List<Demo> findByMember_memberId(Long memberId);

    // 특정 상점과 특정 시간에 예정 상태의 시연 수 조회
    long countByShopIdAndDemoDateAndDemoStatus(Long shopId, LocalDateTime demoDate, DemoStatus demoStatus);
}
