package com.pedalgenie.pedalgenieback.domain.product.presentation;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.image.ImageDirectoryUrl;
import com.pedalgenie.pedalgenieback.domain.image.application.ImageService;
import com.pedalgenie.pedalgenieback.domain.product.application.ProductService;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.ProductCreateRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.*;
import com.pedalgenie.pedalgenieback.domain.product.application.ProductQueryService;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.subcategory.dto.FilterSubCategoryResponse;
import com.pedalgenie.pedalgenieback.global.ResponseTemplate;
import com.pedalgenie.pedalgenieback.global.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductQueryService productQueryService;
    private final TokenProvider tokenProvider;
    private final ImageService imageService;
    private final ProductService productService;

    // 요청 파라미터와 바디 값에 따라 전체 조회, 상위 카테고리 조회 가능, 옵션 설정 가능
    @Operation(summary = "옵션에 따른 상품 목록 조회")
    @GetMapping("/products/search")
    public ResponseEntity<ResponseTemplate<List<GetProductQueryResponse>>> searchProducts(

            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Boolean isRentable,
            @RequestParam(required = false) Boolean isPurchasable,
            @RequestParam(required = false) Boolean isDemoable,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(required = false) List<String> subCategoryNames,
            HttpServletRequest httpRequest,
            Pageable pageable

    ){

        FilterRequest request = FilterRequest.of(
                isRentable,
                isPurchasable,
                isDemoable,
                sortBy,
                subCategoryNames);

        Long memberId = null;
        // 쿠키에서 리프레시 토큰 추출 및 유효성 검증
        String refreshToken = tokenProvider.getRefreshTokenFromRequest(httpRequest);
        Boolean isValid = tokenProvider.isVaildRefreshToken(refreshToken);

        // 리프레시 토큰이 유효할 때 memberId 추출
        if(isValid) {
            memberId = tokenProvider.getMemberIdFromToken(refreshToken);
        }

        List<GetProductQueryResponse> response = productQueryService
                .getProductsByCategory(category,request,memberId,pageable);

        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "옵션에 따른 상품 목록 조회 성공", response);
    }

    @Operation(summary = "이용 범위 옵션 조회")
    @GetMapping("/products/filters")
    public ResponseEntity<ResponseTemplate<FilterMetadataResponse>> getFilterMetadata(){
        FilterResponse filterResponse = productQueryService.getMetadataForFilter();

        FilterMetadataResponse filterMetadataResponse = FilterMetadataResponse.of(filterResponse);
        return  ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "이용 범위 옵션 조회 성공", filterMetadataResponse);
    }

    @Operation(summary = "정렬 옵션 조회")
    @GetMapping("/products/sort-options")
    public ResponseEntity<ResponseTemplate<List<SortBy>>> getSortOptions(){
        List<SortBy> sortOptions = productQueryService.getSortOptions();

        return ResponseTemplate.createTemplate(HttpStatus.OK,true,"정렬 옵션 조회 성공", sortOptions);
    }

    @Operation(summary = "서브 카테고리 옵션 조회")
    @GetMapping("/products/subcategories")
    public ResponseEntity<ResponseTemplate<FilterSubCategoryResponse>> getSubCategories(
            @RequestParam Category category
    ){
        FilterSubCategoryResponse subCategoryProductsResponse = productQueryService.getMetaForSubCategory(category);
        return ResponseTemplate.createTemplate(HttpStatus.OK, true,
                "서브 카테고리 옵션 조회 성공", subCategoryProductsResponse);
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/products/{id}")
    public ResponseEntity<ResponseTemplate<GetProductResponse>> getProduct(@PathVariable Long id,
                                                                           HttpServletRequest request){

        Long memberId = null;
        // 쿠키에서 리프레시 토큰 추출 및 유효성 검증
        String refreshToken = tokenProvider.getRefreshTokenFromRequest(request);
        Boolean isValid = tokenProvider.isVaildRefreshToken(refreshToken);

        // 리프레시 토큰이 유효할 때 memberId 추출
        if(isValid) {
            memberId = tokenProvider.getMemberIdFromToken(refreshToken);
        }

        GetProductResponse getProductResponse = productQueryService.getProductResponse(id, memberId);
        return ResponseTemplate.createTemplate(HttpStatus.OK,true,
                "상품 상세 조회 성공", getProductResponse);
    }

    @Operation(summary = "상품 등록")
    @PostMapping("/admin/products")
    public ResponseEntity<ResponseTemplate<ProductCreateResponse>> createProduct(
            @ModelAttribute ProductCreateRequest request
            ){

        String url = null;
        if(request.descriptionFile() !=null) {
           url = imageService.save(request.descriptionFile(), ImageDirectoryUrl.PRODUCT_DIRECTORY);
        }
        Product product = productService.createProduct(request,url);

        ProductCreateResponse response = ProductCreateResponse.from(product);

        return ResponseTemplate.createTemplate(HttpStatus.CREATED,true,
                "상품 등록 성공", response);

    }



}
