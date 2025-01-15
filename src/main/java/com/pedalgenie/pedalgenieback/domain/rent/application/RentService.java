package com.pedalgenie.pedalgenieback.domain.rent.application;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.available.entity.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.available.repository.AvailableTimeRepository;
import com.pedalgenie.pedalgenieback.domain.rent.repository.RentRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.time.application.TimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus.USED;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class RentService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final TimeService timeService;
    private final RentRepository rentRepository;

    public RentResponse create(final Long memberId, final RentRequest request){
        final Rent rent = convertRent(memberId,request);

        Rent savedRent = rentRepository.save(rent);

        rent.changeAvailableTimeStatus(USED);

        return RentResponse.builder()
                .rentId(savedRent.getId())
                .status(savedRent.getRentStatusType())
                .rentStartTime(savedRent.getRentStartTime())
                .rentEndTime(savedRent.getRentEndTime())
                .pickUpTime(savedRent.getAvailableDateTime().getLocalTime())
                .build();
    }

    private Rent convertRent(final Long memberId, final RentRequest request){
        final Member member = getMember(memberId);
        final Product product = getProduct(request.productId());

        // 해당 일자에 맞는 가게 운영 시간 조회
        Shop shop = product.getShop();
        ShopHours shopHours = timeService.determineShopHours(shop.getId(), request.rentEndDateTime().toLocalDate());

        // 요청된 시간이 운영 시간 내에 있는지 확인
        LocalTime requestedTime = request.rentEndDateTime().toLocalTime();
        if (requestedTime.isBefore(shopHours.getOpenTime()) || requestedTime.isAfter(shopHours.getCloseTime())) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        final Long availableDateTimeId = request.availableDateTimeId();
        final AvailableDateTime availableDateTime = getAvailableDateTimeById(availableDateTimeId);

        product.decreaseStock();

        return Rent.create(availableDateTime,product,member,request.rentEndDateTime());
    }


    private Product getProduct(final Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    private Member getMember(final Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));
    }

    private AvailableDateTime getAvailableDateTimeById(final Long availableDateTimeId){
        return availableTimeRepository.findById(availableDateTimeId)
                .orElseThrow(()-> new CustomException(ErrorCode.AVAILABLE_DATE_TIME_NOT_FOUND));

    }

    // 24시간 이후 FIXED 로 상태 변경


    // 픽업 이후 COMPLETED 로 변경

}
