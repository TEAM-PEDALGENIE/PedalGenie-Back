package com.pedalgenie.pedalgenieback.domain.rent.application;

import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.rent.TimeSlot;
import com.pedalgenie.pedalgenieback.domain.rent.dto.request.RentRequest;
import com.pedalgenie.pedalgenieback.domain.available.dto.AvailableTimeResponse;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.pedalgenie.pedalgenieback.domain.available.entity.AvailableStatus.OPEN;
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

        Product product = rent.getProduct();

        updateAvailableTimeStatus(product, rent);
        // TODO: 동일 상품 예약, 개수가 남아있는데 USED 예외 던지는 것 해결
//        validateUsedTime(rent);

        Rent savedRent = rentRepository.save(rent);

        return RentResponse.builder()
                .rentId(savedRent.getId())
                .status(savedRent.getRentStatusType())
                .rentStartTime(savedRent.getRentStartTime())
                .rentEndTime(savedRent.getRentEndTime())
                .build();
    }

    private void updateAvailableTimeStatus(final Product product, final Rent rent) {
        if (product.getRentQuantity() <= 0) {
            rent.changeAvailableTimeStatus(USED);
        }
    }

    private void validateUsedTime(final Rent rent){

        if(rent.getAvailableDateTime().isUsed() && rent.getProduct().getRentQuantity() <= 0){
            throw new CustomException(ErrorCode.INVALID_USED_BY_OTHER);
        }
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

        // 대여 가능 수량 검증 및 감소
        if (!product.getIsRentable()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }

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

    // 별도로 Available 서비스 작성해서 옮김.
    // (특정 상품의) 대여 가능한 일자 조회
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

                LocalTime openTime = shopHours.getOpenTime();
                LocalTime closeTime = shopHours.getCloseTime();
                LocalTime breakStartTime = shopHours.getBreakStartTime();
                LocalTime breakEndTime = shopHours.getBreakEndTime();

                // 타임슬롯 생성 및 저장
                List<AvailableDateTime> timeSlotsForDate = generateAvailableTimeSlots(productId, targetDate, openTime, closeTime, breakStartTime, breakEndTime);
                availableTimes.addAll(timeSlotsForDate);
            }
        }
        availableTimeRepository.saveAll(availableTimes);  // 모든 타임슬롯을 한 번에 저장

        return AvailableTimeResponse.from(availableTimes);

    }

    private List<AvailableDateTime> generateAvailableTimeSlots(Long productId, LocalDate targetDate, LocalTime openTime, LocalTime closeTime, LocalTime breakStartTime, LocalTime breakEndTime) {

        // 1. TimeSlot 생성
        List<TimeSlot> timeSlots = TimeSlot.generateTimeSlots(openTime, closeTime);

        // 2. 점심시간 예외 처리
        List<TimeSlot> validTimeSlots = TimeSlot.exceptTime(timeSlots, breakStartTime, breakEndTime);

        // 3. validTimeSlots을 이용하여 AvailableTime 엔티티 생성
        List<AvailableDateTime> availableDateTimes = new ArrayList<>();
        for (TimeSlot timeSlot : validTimeSlots) {
            AvailableDateTime availableTime = AvailableDateTime.builder()
                    .productId(productId)
                    .localDate(targetDate) // 날짜만 저장
                    .localTime(timeSlot.getStartTime()) // 시간만 저장
                    .rentStatus(OPEN) // 대여 상태 설정
                    .build();
            availableDateTimes.add(availableTime);
        }
        return availableDateTimes;
    }



//    // 대여 가능한지 확인 후 AvailableDateTime 객체 추가
//    private void checkAndAddAvailableTimes(Long productId, List<AvailableDateTime> availableTimes, LocalDate targetDate) {
//        // 대여 가능한지 확인하는 메서드에서 시작 날짜만 확인
//        LocalDateTime startOfDay = targetDate.atStartOfDay(); // 해당 날짜의 시작 시점
//
//        if (isRentableOnDate(productId, startOfDay)) {
//            availableTimes.add(createAvailableDateTime(productId, startOfDay));
//        }
//    }
//
//    // 대여 가능한지 확인하는 메서드 (시작 날짜만 확인)
//    private boolean isRentableOnDate(Long productId, LocalDateTime startOfDay) {
//        long bookedCount = rentRepository.countByProductIdAndRentStartTime(productId, startOfDay);
//        Product product = getProduct(productId);
//
//        // 대여 시작 날짜에서 대여 수량이 0 이상이면 대여 가능
//        // TODO: 현재는 대여 가능 개수가 없을 때 목록 빈다
//        return product.getIsRentable() && bookedCount < product.getRentQuantity();
//    }
//
//
//    // AvailableDateTime 객체 생성 메서드
//    private AvailableDateTime createAvailableDateTime(Long productId, LocalDateTime dateTime) {
//        return AvailableDateTime.builder()
//                .productId(productId)
//                .localDateTime(dateTime)
//                .rentStatus(OPEN) // 첫 설정
//                .build();
//    }


//    // 대여 가능한 시간 조회 (대여 수량과 관계없음)
//    public List<AvailableDateTime> getAvailableTimes(LocalDateTime startTime, LocalDateTime endTime) {
//        return availableTimeRepository.findAvailableTimesByDateAndStatus(startTime, endTime, OPEN);
//    }

    // 대여 취소





}
