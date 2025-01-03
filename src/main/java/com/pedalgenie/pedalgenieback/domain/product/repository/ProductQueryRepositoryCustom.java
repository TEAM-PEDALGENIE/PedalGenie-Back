package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.service.SortBy;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductQueryRepositoryCustom {


    List<GetProductQueryResponse> findPagingProducts(FilterRequest request);

}
