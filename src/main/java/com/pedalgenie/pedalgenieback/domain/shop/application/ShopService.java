package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopCreateRequest;
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

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopHoursRepository shopHoursRepository;

    public ShopCreateResponse createShop(ShopCreateRequest shopDto, String url){

        Shop shop = shopDto.toEntity(url);

        Shop savedShop = shopRepository.save(shop);

        // ShopHours 저장
        List<ShopHours> shopHoursList = shopDto.toShopHoursEntities(savedShop);
        shopHoursRepository.saveAll(shopHoursList);

        return new ShopCreateResponse(savedShop);
    }
}
