package com.pedalgenie.pedalgenieback.domain.product.presentation;

import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsRequest;
import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.FilterMetadataResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.FilterResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductsResponse;
import com.pedalgenie.pedalgenieback.domain.product.service.ProductQueryService;
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

    @Operation(summary = "옵션 따라 모든 상품 조회")
    @GetMapping
    public ResponseEntity<GetProductsResponse> getProducts(
            @RequestParam(required = false) Boolean isRentable,
            @RequestParam(required = false) Boolean isPurchasable,
            @RequestParam(required = false) Boolean isDemoable,
            @RequestParam(required = false) List<Long> subCategoryIds
    ){
        FilterRequest filterRequest = FilterRequest.of(
                isRentable,
                isPurchasable,
                isDemoable,
                subCategoryIds);

        return ResponseEntity.ok(productQueryService.getProductsByFilters(filterRequest));
    }

    @Operation(summary = "필터 옵션 조회")
    @GetMapping("/filters")
    public ResponseEntity<ResponseTemplate<FilterMetadataResponse>> getFilterMetadata(){
        FilterResponse filterResponse = productQueryService.getMetadataForFilter();
        FilterMetadataResponse filterMetadataResponse = FilterMetadataResponse.of(filterResponse);
        return  ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "필터 옵션 조회 성공", filterMetadataResponse);
    }

    @Operation(summary = "카테고리 옵션 조회")
    @GetMapping("/subcategories")
    public ResponseEntity<ResponseTemplate<FilterSubCategoryResponse>> getSubCategories(){
        FilterSubCategoryResponse subCategoryProductsResponse = productQueryService.getMetaForSubCategory();
        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "카테고리 옵션 조회 성공", subCategoryProductsResponse);
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTemplate<GetProductResponse>> getProduct(@PathVariable Long id){
        GetProductResponse getProductResponse = productQueryService.getProductResponse(id);
        return ResponseTemplate.createTemplate(HttpStatus.OK,true,
                "상품 상세 조회 성공", getProductResponse);
    }

    @Operation(summary ="상위 카테고리 내 상품 조회 ")
    @PostMapping
    public ResponseEntity<ResponseTemplate<List<CategoryProductsResponse>>> getProductsByCategory(
            @RequestBody final CategoryProductsRequest request){
        List<CategoryProductsResponse> responses = productQueryService.getProductByCategory(request);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "상위 카테고리 내 상품 조회 성공",responses);
    }


}
