package com.pedalgenie.pedalgenieback.domain.rent.repository;

import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentRepository extends JpaRepository<Rent, Long> {


    List<Rent> findAllByMemberId(Long memberId);


}
