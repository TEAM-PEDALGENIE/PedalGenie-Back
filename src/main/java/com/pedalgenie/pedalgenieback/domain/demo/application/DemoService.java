package com.pedalgenie.pedalgenieback.domain.demo.application;

import com.pedalgenie.pedalgenieback.domain.demo.dto.request.DemoRequestDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoPatchResponseDto;
import com.pedalgenie.pedalgenieback.domain.demo.dto.response.DemoResponseDto;
import com.pedalgenie.pedalgenieback.domain.demo.entity.Demo;
import com.pedalgenie.pedalgenieback.domain.demo.entity.DemoStatus;
import com.pedalgenie.pedalgenieback.domain.demo.repository.DemoRepository;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import com.pedalgenie.pedalgenieback.global.time.application.TimeService;
import com.pedalgenie.pedalgenieback.global.time.dto.DateDto;
import com.pedalgenie.pedalgenieback.global.time.dto.TimeSlotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DemoService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final DemoRepository demoRepository;
    private final TimeService timeService;
    private final ProductImageQueryService productImageQueryService;

    // 시연 가능 일자 조회 메서드
    public List<DateDto> getAvailableDateList(Long productId) {
        // 시연하려는 상품과 소속 상점 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 시연 가능하지 않은 상품 예외처리
        if (!product.getIsDemoable()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE_FOR_DEMO);
        }

        Long shopId = product.getShop().getId();
        int demoQuantityPerDay = product.getShop().getDemoQuantityPerDay();

        // 오늘 기준으로 28일간 가능 일자 리스트 생성
        LocalDate today = LocalDate.now();
        List<DateDto> availableList = new ArrayList<>();

        for (int i = 1; i <= 28; i++) {
            LocalDate targetDate = today.plusDays(i);

            // ShopHours 조회
            ShopHours shopHours = timeService.determineShopHours(shopId, targetDate);

            boolean isAvailable = false;

            if (shopHours != null) {
                // 운영 시간이 존재하면 30분 단위로 가능한 시간 확인
                LocalTime currentTime = shopHours.getOpenTime();
                LocalTime closeTime = shopHours.getCloseTime();

                while (!currentTime.isAfter(closeTime)) {
                    // 점심시간 제외 처리
                    if (shopHours.getBreakStartTime() != null && shopHours.getBreakEndTime() != null) {
                        if (!currentTime.isBefore(shopHours.getBreakStartTime()) && !currentTime.isAfter(shopHours.getBreakEndTime())) {
                            currentTime = currentTime.plusMinutes(30);
                            continue;
                        }
                    }
                    LocalDateTime demoDateTime = LocalDateTime.of(targetDate, currentTime);

                    // SCHEDULED 상태의 예약 수량 확인
                    long bookedCount = demoRepository.countByShopIdAndDemoDateAndDemoStatus(shopId, demoDateTime, DemoStatus.SCHEDULED);

                    if (bookedCount < demoQuantityPerDay) {
                        isAvailable = true;
                        break; // 가능한 시간이 있으면 바로 다음 날짜로 이동
                    }
                    // 30분 단위로 시간 증가
                    currentTime = currentTime.plusMinutes(30);
                }
            }
            DateDto dateAvailability = DateDto.builder()
                    .date(targetDate)
                    .isAvailable(isAvailable)
                    .build();
            availableList.add(dateAvailability);
        }
        return availableList;
    }


    // 특정 일자의 시연 가능 시간 조회 메서드
    public List<TimeSlotDto> generateDemoAvailableSlots(Long productId, LocalDate date) {
        // 시연하려는 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 시연 가능하지 않은 상품 예외처리
        if (!product.getIsDemoable()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE_FOR_DEMO);
        }
        List<TimeSlotDto> availableSlots = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 당일 조회 예외처리
        if (date.isEqual(LocalDate.now())) {
            return availableSlots;
        }

        // 시연하려는 상품의 가게 조회
        Shop shop = product.getShop();
        Long shopId = shop.getId();
        int demoQuantityPerDay = shop.getDemoQuantityPerDay();

        // 가게 운영 시간 조회
        ShopHours shopHours = timeService.determineShopHours(shopId, date);

        // 가게 운영 시간이 없으면 빈 리스트 반환
        if (shopHours == null) {
            return availableSlots;
        }

        // 운영 시간 기반으로 30분 단위 시간대 생성 및 가능 여부 확인
        LocalTime currentTime = shopHours.getOpenTime();
        LocalTime closeTime = shopHours.getCloseTime();

        while (!currentTime.isAfter(closeTime)) {
            // 점심시간 제외 처리
            if (shopHours.getBreakStartTime() != null && shopHours.getBreakEndTime() != null) {
                if (!currentTime.isBefore(shopHours.getBreakStartTime()) && !currentTime.isAfter(shopHours.getBreakEndTime())) {
                    currentTime = currentTime.plusMinutes(30);
                    continue;
                }
            }

            LocalDateTime slotDateTime = LocalDateTime.of(date, currentTime);

            // 24시간 내 시간 제외
            if (slotDateTime.isBefore(currentDateTime.plusHours(24))) {
                currentTime = currentTime.plusMinutes(30);
                continue;
            }

            // SCHEDULED 상태의 예약 수량 확인
            long bookedCount = demoRepository.countByShopIdAndDemoDateAndDemoStatus(shopId, slotDateTime, DemoStatus.SCHEDULED);

            // 가능한 시간대 여부 확인
            boolean isAvailable = bookedCount < demoQuantityPerDay;

            TimeSlotDto slot = TimeSlotDto.builder()
                    .slotTime(currentTime)
                    .isAvailable(isAvailable)
                    .build();
            availableSlots.add(slot);

            // 30분 단위로 시간 증가
            currentTime = currentTime.plusMinutes(30);
        }

        return availableSlots;
    }


    @Transactional
    // 시연 생성 메서드
    public DemoResponseDto createDemo(DemoRequestDto requestDto, Long memberId) {
        // 시연하려는 상품 조회
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 시연 가능하지 않은 상품 예외처리
        if (!product.getIsDemoable()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE_FOR_DEMO);
        }

        // 24시간 내 생성 요청 예외처리
        if (requestDto.getDemoDate().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        // 시연 생성하려는 유저 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        Shop shop = product.getShop();
        int demoQuantityPerDay = shop.getDemoQuantityPerDay();

        // 해당 일자에 맞는 가게 운영 시간 조회
        ShopHours shopHours = timeService.determineShopHours(shop.getId(), requestDto.getDemoDate().toLocalDate());

        // 운영 시간이 없는 경우 예외 처리
        if (shopHours == null) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        // 요청된 시간이 운영 시간 내에 있는지 확인
        LocalTime requestedTime = requestDto.getDemoDate().toLocalTime();
        if (requestedTime.isBefore(shopHours.getOpenTime()) || requestedTime.isAfter(shopHours.getCloseTime())) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        // 요청 시간이 점심 시간이 아닌지 확인
        if (shopHours.getBreakStartTime() != null && shopHours.getBreakEndTime() != null) {
            if (!requestedTime.isBefore(shopHours.getBreakStartTime()) && !requestedTime.isAfter(shopHours.getBreakEndTime())) {
                throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
            }
        }

        // SCHEDULED 상태 예약 수량 확인
        long bookedCount = demoRepository.countByShopIdAndDemoDateAndDemoStatus(shop.getId(), requestDto.getDemoDate(), DemoStatus.SCHEDULED);
        if (bookedCount >= demoQuantityPerDay) {
            throw new CustomException(ErrorCode.NOT_AVAILABLE_TIME);
        }

        // Demo 엔티티 생성 및 저장
        Demo demo = Demo.builder()
                .product(product)
                .member(member)
                .shop(shop)
                .demoDate(requestDto.getDemoDate())
                .demoStatus(DemoStatus.SCHEDULED)
                .build();

        Demo savedDemo = demoRepository.save(demo);

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
                .productThumbnailImageUrl(productImageQueryService.getFirstProductImage(demo.getProduct().getId()).imageUrl())
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
                        .productThumbnailImageUrl(productImageQueryService.getFirstProductImage(demo.getProduct().getId()).imageUrl())
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

    // 특정 유저의 시연 전체 취소 메서드
    @Transactional
    public void cancelDemosByMemberId(Long memberId) {
        // memberId로 demo 리스트 조회
        List<Demo> demos = demoRepository.findByMember_memberId(memberId);
        if (demos.isEmpty()) {
            return;
        }
        // 시연예정 상태 demo를 취소로 변경
        for (Demo demo : demos) {
            if (demo.getDemoStatus() == DemoStatus.SCHEDULED) {
                demo.updateDemoStatus(DemoStatus.CANCELED);
            }
        }
    }
}

