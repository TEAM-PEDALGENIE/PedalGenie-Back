package com.pedalgenie.pedalgenieback.domain.rent.repository;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentRepository extends JpaRepository<Rent, Long> {


    @Query("SELECT r FROM Rent r WHERE r.member.id = :memberId")
    List<Rent> findAllByMemberId(@Param("memberId") Long memberId);
}

