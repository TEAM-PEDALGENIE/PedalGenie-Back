package com.pedalgenie.pedalgenieback.domain.product.application;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.*;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductQueryRepositoryCustom;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.domain.subcategory.dto.FilterSubCategoryResponse;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.domain.subcategory.repository.SubcategoryRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ProductQueryRepositoryCustom productQueryRepository;
    private final ProductImageQueryService productImageQueryService;
    private final ShopRepository shopRepository;


    // 전체, 상위 카테고리별 목록 조회(필터, 정렬, 서브 카테고리 옵션)
    public List<GetProductQueryResponse> getProductsByCategory(
            Category category,
            FilterRequest request){

        return productQueryRepository.findPagingProducts(category, request);

    }

    // 이용 범위 옵션 조회
    public FilterResponse getMetadataForFilter(){
        return FilterResponse.of();
    }

    // 서브 카테고리 옵션 조회
    public FilterSubCategoryResponse getMetaForSubCategory(Category category){
        List<SubCategory> subCategories = subcategoryRepository.findByCategory(category);
        return FilterSubCategoryResponse.of(subCategories);
    }

    // 정렬 옵션 조회
    public List<SortBy> getSortOptions() {
        return SortBy.getAllSortOptions();
    }

    // 상품 상세 조회
    public GetProductResponse getProductResponse(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        List<ProductImageDto> productImages = productImageQueryService.getProductImages(id);
        return GetProductResponse.of(product, productImages);
    }

    // 상품 목록
    public List<ProductResponse> getProductsByShop(Long shopId) {

        List<Product> products = productRepository.findByShopId(shopId);

        // 조회한 Product를 GetProductQueryResponse로 변환
        List<GetProductQueryResponse> getProductQueryResponses = products.stream()
                .map(product -> new GetProductQueryResponse(
                        product.getId(),
                        product.getShop().getShopname(),
                        product.getName(),
                        product.getRentPricePerDay(),
                        product.getIsRentable(),
                        product.getIsPurchasable(),
                        product.getIsDemoable(),
                        product.getThumbnailImageUrl() // 여기 추가
                ))
                .toList();


        // 조회한 상품들을 ProductResponse로 변환하여 반환
        List<ProductResponse> productResponses = getProductQueryResponses.stream()
                .map(product -> ProductResponse.from(
                        product,
                        productImageQueryService.getFirstProductImage(product.id())
                ))
                .toList();

        return productResponses;
    }

}
