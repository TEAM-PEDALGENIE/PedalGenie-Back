package com.pedalgenie.pedalgenieback.domain.fcm.repository;

import com.pedalgenie.pedalgenieback.domain.fcm.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Optional<Fcm> findByMember_MemberId(Long memberId);
}
