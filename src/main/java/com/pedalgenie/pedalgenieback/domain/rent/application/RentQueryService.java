package com.pedalgenie.pedalgenieback.domain.rent.application;

import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentDetailResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentListResponse;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.repository.RentRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RentQueryService {

    private final RentRepository rentRepository;
    private final ProductImageQueryService productImageQueryService;


    // 대여 상세 조회
    @Transactional(readOnly = true)
    public RentDetailResponse getRentDetail(Long rentId){
        Rent rent = getRent(rentId);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    // 대여 목록 조회
    @Transactional(readOnly = true)
    public List<RentListResponse> getRentList(Long memberId) {

        List<Rent> rents = rentRepository.findAllByMemberId(memberId);


        return rents.stream()
                .map(rent -> {
                    // 상품 이미지 URL 가져오기
                    String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();
                    return RentListResponse.from(rent, productImageUrl);
                })
                .toList();
    }

    // 어드민용, 대여 상태 픽업 예정으로 변경
    public RentDetailResponse updateRentStatusToPickUp(Long rentId){
        Rent rent = getRent(rentId);

        rent.updateToPickUp();
        rentRepository.save(rent);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    // 직원 확인용, 사용 중으로 변경
    public RentDetailResponse updateRentStatusToInUse(Long rentId){
        Rent rent = getRent(rentId);

        rent.updateToRent();
        rentRepository.save(rent);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    // 직원 확인용, 반납 완료로 변경
    public RentDetailResponse updateRentStatusToReturned(Long rentId){
        Rent rent = getRent(rentId);

        rent.updateToRETURND();
        rentRepository.save(rent);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    // 어드민용, 취소 접수로 변경
    public RentDetailResponse cancelRequestRent(Long rentId){
        Rent rent = getRent(rentId);

        rent.cancelRequested();
        rentRepository.save(rent);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    // 어드민용, 취소 완료로 변경
    public RentDetailResponse cancelCompletedRent(Long rentId){
        Rent rent = getRent(rentId);

        rent.cancelCompleted();
        rentRepository.save(rent);

        String productImageUrl = productImageQueryService.getFirstProductImage(rent.getProduct().getId()).imageUrl();

        return RentDetailResponse.from(rent, productImageUrl);
    }

    private Rent getRent(final Long rentId){
        return rentRepository.findById(rentId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_RENT));
    }

}
