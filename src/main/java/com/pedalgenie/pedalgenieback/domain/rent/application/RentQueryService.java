package com.pedalgenie.pedalgenieback.domain.rent.application;

import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentDetailResponse;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.repository.RentRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentQueryService {

    private final RentRepository rentRepository;


    // 대여 상세 조회
    public RentDetailResponse getRentDetail(Long rentId){
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_RENT));

        return new RentDetailResponse(
                rent.getId(),
                rent.getRentStartTime(),
                rent.getRentEndTime(),
                rent.getProduct().getPrice(),
                rent.getMember().getNickname(),
                LocalDate.now(),
                rent.getRentStatusType().name(),
                rent.getProduct().getName(),
                rent.getProduct().getShop().getShopname()
        );
    }



}
