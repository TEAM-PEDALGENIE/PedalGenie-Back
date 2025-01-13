package com.pedalgenie.pedalgenieback.domain.rent.application;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.AvailableTimeResponse;
import com.pedalgenie.pedalgenieback.domain.rent.dto.response.RentResponse;
import com.pedalgenie.pedalgenieback.domain.rent.entity.Rent;
import com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableDateTime;
import com.pedalgenie.pedalgenieback.domain.rent.repository.AvailableTimeRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableStatus.OPEN;
import static com.pedalgenie.pedalgenieback.domain.rent.entity.availableTime.AvailableStatus.USED;

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
        validateUsedTime(rent);

        // 예약처리
        rent.changeAvailableTimeStatus(USED);

        Rent savedRent = rentRepository.save(rent);

        return RentResponse.builder()
                .rentId(savedRent.getId())
                .status(savedRent.getRentStatusType())
                .rentStartTime(savedRent.getRentStartTime())
                .rentEndTime(savedRent.getRentEndTime())
                .build();
    }


    private Rent convertRent(final Long memberId, final RentRequest request){
        final Member member = getMember(memberId);
        final Product product = getProduct(request.productId());

        // 48시간 내 생성 요청 예외처리
        if (request.rentEndDateTime().isBefore(LocalDateTime.now().plusHours(48))) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

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

        return Rent.create(availableDateTime,product,member);
    }

    private void validateUsedTime(final Rent rent){

        if(rent.getAvailableDateTime().isUsed()){
            throw new CustomException(ErrorCode.INVALID_USED_BY_OTHER);
        }
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

    // (특정 상품의) 대여 가능한 일자 조회
    @Transactional(readOnly = true)
    public List<AvailableTimeResponse> findAvailableDateTimes(final Long productId) {

        // 대여하려는 상품과 상점 조회
        Product product = getProduct(productId);
        Long shopId = product.getShop().getId();


        // 오늘 기준으로 28일간 가능 일자 리스트 생성
        LocalDate today = LocalDate.now();
        List<AvailableDateTime> availableTimes = new ArrayList<>();

        // 각 날짜가 대여 가능한지
        for (int i = 1; i <= 28; i++) {
            LocalDate targetDate = today.plusDays(i);

            // ShopHours 조회
            ShopHours shopHours = timeService.determineShopHours(shopId, targetDate);

            if (shopHours != null) {
                // 해당 날짜에 예약된 대여 수량 확인
                checkAndAddAvailableTimes(productId, availableTimes, targetDate);

            }
        }
            return AvailableTimeResponse.from(availableTimes);

    }

    // 대여 가능한지 확인 후 AvailableDateTime 객체 추가
    private void checkAndAddAvailableTimes(Long productId, List<AvailableDateTime> availableTimes, LocalDate targetDate) {
        // 대여 가능한지 확인하는 메서드에서 시작 날짜만 확인
        LocalDateTime startOfDay = targetDate.atStartOfDay(); // 해당 날짜의 시작 시점

        if (isRentableOnDate(productId, startOfDay)) {
            availableTimes.add(createAvailableDateTime(productId, startOfDay));
        }
    }

    // 대여 가능한지 확인하는 메서드 (시작 날짜만 확인)
    private boolean isRentableOnDate(Long productId, LocalDateTime startOfDay) {
        long bookedCount = rentRepository.countByProductIdAndRentStartTime(productId, startOfDay);
        Product product = getProduct(productId);
        // 대여 시작 날짜에서 대여 수량이 0 이상이면 대여 가능
        // TODO: 대여 가능 개수가 없을 때 에러 반환
        return product.isRentable() && bookedCount < product.getRentQuantity();
    }



    // AvailableDateTime 객체 생성 메서드
    private AvailableDateTime createAvailableDateTime(Long productId, LocalDateTime dateTime) {
        return AvailableDateTime.builder()
                .productId(productId)
                .localDateTime(dateTime)
                .rentStatus(OPEN) // 첫 설정
                .build();
    }





}
