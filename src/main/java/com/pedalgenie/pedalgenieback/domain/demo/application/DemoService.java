package com.pedalgenie.pedalgenieback.domain.demo.application;

import com.pedalgenie.pedalgenieback.domain.demo.dto.request.DemoRequestDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoPatchResponseDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoResponseDto;
import com.pedalgenie.pedalgenieback.domain.demo.entity.Demo;
import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoSlot;
import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoStatus;
import com.pedalgenie.pedalgenieback.domain.demo.repository.DemoRepository;
import com.pedalgenie.pedalgenieback.domain.demo.repository.DemoSlotRepository;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopHoursRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.time.dto.DateDto;
import com.pedalgenie.pedalgenieback.global.time.entity.DayType;
import com.pedalgenie.pedalgenieback.global.time.dto.TimeSlotDto;
import com.pedalgenie.pedalgenieback.global.time.application.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DemoService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ShopHoursRepository shopHoursRepository;

    private final DemoSlotRepository demoSlotRepository;
    private final DemoRepository demoRepository;
    private final DemoSlotService demoSlotService;
    private final HolidayService holidayService;

    // 시연 가능 일자 조회 메서드
    public List<DateDto> getAvailableDateList(Long productId){
        // 시연하려는 상품과 소속 상점 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        Long shopId = product.getShop().getId();

        // 오늘 기준으로 28일간 가능 일자 리스트 생성
        LocalDate toDay = LocalDate.now();
        List<DateDto> availableList = new ArrayList<>();

        for (int i = 1; i <= 28; i++) {
            LocalDate targetDate = toDay.plusDays(i);

            // 공휴일 여부 확인
            boolean isHoliday = holidayService.isHoliday(targetDate);

            // ShopHours 조회, HOLIDAY인지 확인
            ShopHours holidayHours = shopHoursRepository.findByShopIdAndDayType(shopId, DayType.HOLIDAY).orElse(null);

            // 공휴일이지만 HOLIDAY, 운영 시간이 있는 경우
            boolean isHolidayAvailable = isHoliday && holidayHours != null;

            // 공휴일이 아니거나, HOLIDAY 운영 시간이 설정된 경우만 가능한 슬롯 개수 확인
            long availableCount = (isHolidayAvailable || !isHoliday) ? demoSlotRepository.countAvailableSlots(shopId, targetDate) : 0;

            // 가능한 슬롯이 있는 경우 true
            boolean isAvailable = availableCount > 0;

            DateDto dateAvailability = DateDto.builder()
                    .date(targetDate)
                    .available(isAvailable)
                    .build();
            availableList.add(dateAvailability);
        }
        return availableList;
    }

    // 특정 일자의 시연 가능 시간 조회 메서드
    public List<TimeSlotDto> generateDemoAvailableSlots(Long productId, LocalDate date) {
        List<TimeSlotDto> availableSlots = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 요청 날짜가 공휴일이고, 운영 시간이 없으면 예외 발생
        if (holidayService.isHoliday(date)) {
            throw new CustomException(ErrorCode.NOT_FOUND_SHOP_HOURS);
        }

        // 당일 조회 예외처리
        if (date.isEqual(LocalDate.now())) {
            return availableSlots;
        }

        // 시연하려는 상품과 소속 상점 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        Shop shop = product.getShop();
        Long shopId = shop.getId();
        int demoQuantityPerDay = shop.getDemoQuantityPerDay();

        // 해당 날짜의 DemoSlot 조회
        List<DemoSlot> demoSlots = demoSlotService.getDemoSlotsByShopAndDate(shopId, date);

        // 운영 시간이 없으면 빈 리스트 반환
        if (demoSlots.isEmpty()) {
            return availableSlots;
        }

        for (DemoSlot demoSlot : demoSlots) {
            // 24시간 내 가능 시간대 제외
            LocalDateTime slotDateTime = LocalDateTime.of(demoSlot.getDemoDate(), demoSlot.getTimeSlot());
            if (slotDateTime.isBefore(currentDateTime.plusHours(24))) {
                continue;
            }

            // 가능한 시간대이고, 최대 수량보다 작은 타임만 가능
            boolean isAvailable = (demoSlot.isAvailable()) && (demoSlot.getBookedQuantity() < demoQuantityPerDay);
            TimeSlotDto slot = TimeSlotDto.builder()
                    .slotTime(demoSlot.getTimeSlot().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .isAvailable(isAvailable)
                    .build();
            availableSlots.add(slot);
        }
        return availableSlots;
    }


    @Transactional
    // 시연 생성 메서드
    public DemoResponseDto createDemo(DemoRequestDto requestDto, Long memberId) {
        // 요청 날짜가 공휴일이고, 운영 시간이 없으면 예외 발생
        if (holidayService.isHoliday(requestDto.getDemoDate().toLocalDate())) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SLOT);
        }

        // 시연하려는 상품 조회
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 시연 가능하지 않은 상품 예외처리
        if (Boolean.FALSE.equals(product.getIsDemoable())) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE_FOR_DEMO);
        }

        // 24시간 내 생성 요청 예외처리
        LocalDateTime requestedDemoDate = requestDto.getDemoDate();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (requestedDemoDate.isBefore(currentDateTime.plusHours(24))) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SLOT);
        }

        // 시연 생성하려는 유저, 상품의 가게 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        Shop shop = product.getShop();
        int demoQuantityPerDay = shop.getDemoQuantityPerDay();

        // 시연 가능한 slot인지 조회
        DemoSlot demoSlot = demoSlotRepository.findByShopIdAndDemoDateAndTimeSlot(
                shop.getId(),
                requestedDemoDate.toLocalDate(),
                requestedDemoDate.toLocalTime()
        ).orElseThrow(() -> new CustomException(ErrorCode.NOT_AVAILABLE_SLOT));

        if (!demoSlot.isAvailable()) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_SLOT);
        }

        // Demo 엔티티 생성
        Demo demo = Demo.builder()
                .product(product)
                .member(member)
                .shop(shop)
                .demoDate(requestDto.getDemoDate())
                .demoStatus(DemoStatus.SCHEDULED)
                .build();

        // Demo 저장
        Demo savedDemo = demoRepository.save(demo);

        // DemoSlot 상태 갱신
        demoSlot.markAsBooked(demoQuantityPerDay);

        return DemoResponseDto.builder()
                .demoId(savedDemo.getDemoId())
                .demoStatus(savedDemo.getDemoStatus().getStatusDescription())
                .demoDate(savedDemo.getDemoDate())
                .productName(product.getName())
                .shopName(shop.getShopname())
                .memberNickName(member.getNickname())
                .build();
    }

    // 예약 시간이 지난 (3시간 이후) 시연 예정을 확인하고 완료로 변경하는 메서드
    @Transactional
    public void updateDemoStatusIfRequired(Demo demo) {
        if (demo.getDemoDate().isBefore(LocalDateTime.now()) && demo.getDemoStatus() == DemoStatus.SCHEDULED) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime changeTime = demo.getDemoDate().plusHours(3);
            if (now.isAfter(changeTime) && demo.getDemoStatus() == DemoStatus.SCHEDULED) {
                demo.updateDemoStatus(DemoStatus.COMPLETED);
            }
        }
    }

    // 시연 조회 메서드
    @Transactional
    public DemoResponseDto getDemo(Long demoId, Long memberId) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEMO));

        // 시연 요청자와 일치하지 않으면 조회 불가
        if (!demo.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        // 예약 시간이 지난 시연 예정은 시연 완료로 상태 변경
        updateDemoStatusIfRequired(demo);

        // 시연 정보와 함께 상세 응답 DTO 생성
        return DemoResponseDto.builder()
                .demoId(demo.getDemoId())
                .demoStatus(demo.getDemoStatus().getStatusDescription())
                .demoDate(demo.getDemoDate())
                .reservedDate(demo.getCreatedAt().toLocalDate())
                .productName(demo.getProduct().getName())
                .productThumbnailImageUrl(demo.getProduct().getThumbnailImageUrl())
                .shopName(demo.getShop().getShopname())
                .shopAddress(demo.getShop().getAddress())
                .memberNickName(demo.getMember().getNickname())
                .build();
    }

    // 시연 목록 조회 메서드
    @Transactional
    public List<DemoResponseDto> getDemoList(Long memberId) {
        List<Demo> demos = demoRepository.findByMember_memberId(memberId);

        // 예약 시간이 지난 시연 예정은 시연 완료로 상태 변경
        demos.forEach(this::updateDemoStatusIfRequired);

        return demos.stream()
                .map(demo -> DemoResponseDto.builder()
                        .demoId(demo.getDemoId())
                        .demoStatus(demo.getDemoStatus().getStatusDescription())
                        .demoDate(demo.getDemoDate())
                        .productName(demo.getProduct().getName())
                        .productThumbnailImageUrl(demo.getProduct().getThumbnailImageUrl())
                        .shopName(demo.getShop().getShopname())
                        .shopAddress(demo.getShop().getAddress())
                        .build())
                .collect(Collectors.toList());
    }


    // 시연 상태 수정 메서드
    @Transactional
    public DemoPatchResponseDto updateDemoStatus(Long demoId, Long memberId){
        // 시연 조회
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEMO));

        // 시연 상태가 시연 예정이 아니면 변경 불가
        if (demo.getDemoStatus() != DemoStatus.SCHEDULED) {
            throw new CustomException(ErrorCode.INVALID_DEMO_STATUS_UPDATE);
        }

        // 시연 시간 이전에는 완료로 변경 불가
        LocalDateTime now = LocalDateTime.now();
        if (demo.getDemoDate().isAfter(now)) {
            throw new CustomException(ErrorCode.INVALID_DEMO_TIME_UPDATE);
        }

        // 시연 요청자와 일치하지 않으면 변경 불가
        if (!demo.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        // 시연 상태 수정
        demo.updateDemoStatus(DemoStatus.COMPLETED);
        demoRepository.save(demo);

        return DemoPatchResponseDto.builder()
                .demoId(demo.getDemoId())
                .demoStatus(DemoStatus.COMPLETED.getStatusDescription())
                .demoDate(demo.getDemoDate())
                .editedDate(now)
                .build();
    }

    // 어드민 - 시연 상세 조회 메서드
    @Transactional
    public DemoResponseDto getAdminDemo(Long demoId) {
        // 시연 조회
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEMO));

        // 예약 시간이 지난 시연 예정은 시연 완료로 상태 변경
        updateDemoStatusIfRequired(demo);

        return DemoResponseDto.builder()
                .demoId(demo.getDemoId())
                .demoStatus(demo.getDemoStatus().getStatusDescription())
                .demoDate(demo.getDemoDate())
                .productName(demo.getProduct().getName())
                .shopName(demo.getShop().getShopname())
                .memberId(demo.getMember().getMemberId())
                .memberNickName(demo.getMember().getNickname())
                .build();
    }


    // 어드민 - 시연 목록 조회 메서드
    @Transactional
    public List<DemoResponseDto> getAdminDemoList() {
        // 시연 전체 조회
        List<Demo> demos = demoRepository.findAll();

        // 예약 시간이 지난 시연 예정은 시연 완료로 상태 변경
        demos.forEach(this::updateDemoStatusIfRequired);

        return demos.stream()
                .map(demo -> DemoResponseDto.builder()
                        .demoId(demo.getDemoId())
                        .demoStatus(demo.getDemoStatus().getStatusDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    // 어드민 - 시연 상태 수정 메서드
    public DemoPatchResponseDto updateAdminDemoStatus(Long demoId, String status){
        // 시연 조회
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEMO));

        // 상태가 "취소"로 수정되는 경우, 타임슬롯 명수에서 -1
        if (status.equals("CANCELED")) {
            DemoSlot demoSlot = demoSlotRepository.findByShopIdAndDemoDateAndTimeSlot(
                    demo.getShop().getId(),
                    demo.getDemoDate().toLocalDate(),
                    demo.getDemoDate().toLocalTime()
            ).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEMO));

            // 타임슬롯에서 예약된 수량 -1
            demoSlot.decreaseBookedQuantity();
        }

        // 상태 수정
        DemoStatus newStatus = DemoStatus.valueOf(status);
        demo.updateDemoStatus(newStatus);

        LocalDateTime editedDate = LocalDateTime.now();

        return DemoPatchResponseDto.builder()
                .demoId(demo.getDemoId())
                .demoStatus(demo.getDemoStatus().getStatusDescription())
                .demoDate(demo.getDemoDate())
                .editedDate(editedDate)
                .build();
    }
}

