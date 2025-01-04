package com.pedalgenie.pedalgenieback.domain.product.presentation;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.*;
import com.pedalgenie.pedalgenieback.domain.product.application.ProductQueryService;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.pedalgenie.pedalgenieback.domain.subcategory.dto.FilterSubCategoryResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductQueryService productQueryService;

    // 요청 파라미터와 바디 값에 따라 전체 조회, 상위 카테고리 조회 가능, 옵션 설정 가능
    @Operation(summary = "옵션에 따른 상품 목록 조회")
    @PostMapping("/search")
    public ResponseEntity<ResponseTemplate<List<GetProductQueryResponse>>> searchProducts(

            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Boolean isRentable,
            @RequestParam(required = false) Boolean isPurchasable,
            @RequestParam(required = false) Boolean isDemoable,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) List<Long> subCategoryIds

    ){

        FilterRequest request = FilterRequest.of(
                isRentable,
                isPurchasable,
                isDemoable,
                sortBy,
                subCategoryIds);

        List<GetProductQueryResponse> response = productQueryService.getProductsByCategory(category,request);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "옵션에 따른 상품 목록 조회 성공", response);
    }

    @Operation(summary = "이용 범위 옵션 조회")
    @GetMapping("/filters")
    public ResponseEntity<ResponseTemplate<FilterMetadataResponse>> getFilterMetadata(){
        FilterResponse filterResponse = productQueryService.getMetadataForFilter();

        FilterMetadataResponse filterMetadataResponse = FilterMetadataResponse.of(filterResponse);
        return  ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "이용 범위 옵션 조회 성공", filterMetadataResponse);
    }

    @Operation(summary = "정렬 옵션 조회")
    @GetMapping("/sort-options")
    public ResponseEntity<ResponseTemplate<List<SortBy>>> getSortOptions(){
        List<SortBy> sortOptions = productQueryService.getSortOptions();

        return ResponseTemplate.createTemplate(HttpStatus.OK,true,"정렬 옵션 조회 성공", sortOptions);
    }

    @Operation(summary = "서브 카테고리 옵션 조회")
    @GetMapping("/subcategories")
    public ResponseEntity<ResponseTemplate<FilterSubCategoryResponse>> getSubCategories(
            @RequestParam Category category
    ){
        FilterSubCategoryResponse subCategoryProductsResponse = productQueryService.getMetaForSubCategory(category);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "서브 카테고리 옵션 조회 성공", subCategoryProductsResponse);
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTemplate<GetProductResponse>> getProduct(@PathVariable Long id){
        GetProductResponse getProductResponse = productQueryService.getProductResponse(id);
        return ResponseTemplate.createTemplate(HttpStatus.OK,true,
                "상품 상세 조회 성공", getProductResponse);
    }


}
