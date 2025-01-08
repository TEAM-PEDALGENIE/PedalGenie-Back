package com.pedalgenie.pedalgenieback.domain.shop.application;

import com.pedalgenie.pedalgenieback.domain.shop.dto.request.ShopCreateRequest;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.ShopCreateResponse;
import com.pedalgenie.pedalgenieback.domain.shop.dto.response.ShopResponse;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopCreateResponse createShop(ShopCreateRequest shopDto, String url){

        Shop shop = shopDto.toEntity(url);

        Shop savedShop = shopRepository.save(shop);

        return new ShopCreateResponse(savedShop);
    }
}
