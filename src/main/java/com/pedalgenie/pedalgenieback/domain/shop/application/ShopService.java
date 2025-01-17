package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopCreateRequest;
import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopHoursDto;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.ShopCreateResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopHoursRepository;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopHoursRepository shopHoursRepository;

    public ShopCreateResponse createShop(ShopCreateRequest shopDto, String url){

        Shop shop = shopDto.toEntity(url);

        Shop savedShop = shopRepository.save(shop);

        // ShopHoursDto를 ShopHours로 변환
        List<ShopHours> shopHoursList = shopDto.getShopHours().stream()
                .map(shopHoursDto -> toShopHoursEntity(shopHoursDto, savedShop))  // savedShop을 ShopHours에 넣기
                .collect(Collectors.toList());

        shopHoursRepository.saveAll(shopHoursList);

        Shop shopWithHours = shopRepository.findById(savedShop.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));

        return ShopCreateResponse.from(shopWithHours);
    }

    // ShopHoursDto를 ShopHours로 변환하는 메서드
    private ShopHours toShopHoursEntity(ShopHoursDto dto, Shop shop) {
        return ShopHours.builder()
                .shop(shop)  // ShopHours에 연결된 Shop 설정
                .dayType(dto.getDayType())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .breakStartTime(dto.getBreakStartTime())
                .breakEndTime(dto.getBreakEndTime())
                .build();
    }
}
