package com.pedalgenie.pedalgenieback.domain.product.dto.response;

import java.util.List;

public record FilterResponse(
        List<Boolean> rentOptions,
        List<Boolean> purchaseOptions,
        List<Boolean> demoOptions

) {
    public static FilterResponse of(){
        return new FilterResponse(
                List.of(true, false),
                List.of(true, false),
                List.of(true, false)
        );
    }
}
