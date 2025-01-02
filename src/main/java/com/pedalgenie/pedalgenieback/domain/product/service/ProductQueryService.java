package com.pedalgenie.pedalgenieback.domain.product.service;

import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsRequest;
import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsResponse;
import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.category.repository.CategoryRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.*;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductQueryRepositoryCustom;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.applicatioin.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.productImage.applicatioin.ProductImageQueryService;
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
    private final CategoryRepository categoryRepository;
    private final ProductImageQueryService productImageQueryService;
    private final ShopRepository shopRepository;

    // 특정 상위 카테고리의 상품 조회
    public List<CategoryProductsResponse> getProductByCategory(final CategoryProductsRequest request){
        final Category findCategory = getCategory(request.categoryId());

        final List<Product> products = productRepository.findAllBySubCategoryCategoryId(findCategory.getId());

        return products.stream()
                .map(CategoryProductsResponse::from)
                .toList();
    }

    private Category getCategory(final Long categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    // 필터 조건에 따른 상품 조회
    public GetProductsResponse getProductsByFilters(FilterRequest filterDto){
        List<GetProductQueryResponse> products = getPagingProducts(filterDto);

        List<ProductResponse> productResponses = products.stream()
                .map(product -> ProductResponse.from(
                        product,
                        productImageQueryService.getFirstProductImage(product.id())
                ))
                .toList();

        return GetProductsResponse.from(productResponses);

    }

    public List<GetProductQueryResponse> getPagingProducts(FilterRequest filterDto){
        return productQueryRepository.findPagingProducts(
                filterDto.isRentable(),
                filterDto.isPurchasable(),
                filterDto.isDemoable(),
                filterDto.subCategoryIds()
        );
    }

    // 필터 옵션 조회
    public FilterResponse getMetadataForFilter(){
        return FilterResponse.of();
    }

    // 카테고리 옵션 조회
    public FilterSubCategoryResponse getMetaForSubCategory(){
        List<SubCategory> subCategories = subcategoryRepository.findAll();
        return FilterSubCategoryResponse.of(subCategories);
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
                        product.getIsDemoable()
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
