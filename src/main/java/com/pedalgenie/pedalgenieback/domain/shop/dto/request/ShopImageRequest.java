package com.pedalgenie.pedalgenieback.domain.shop.dto.request;

import com.pedalgenie.pedalgenieback.domain.shop.entity.ShopHours;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopImageRequest{
        private String shopname;
        private String address;
        private String contactNumber;
        private Integer demoQuantityPerDay;
        private MultipartFile imageUrl;
        private String detailAddress;
        private String description;
        private Integer instrumentCount;
        private List<ShopHoursDto> shopHour;

    // ShopCreateRequest로 변환
    public ShopCreateRequest toCreateRequest() {
        return new ShopCreateRequest(
                shopname,
                address,
                contactNumber,
                demoQuantityPerDay,
                detailAddress,
                description,
                instrumentCount,
                shopHour
        );
    }
}


