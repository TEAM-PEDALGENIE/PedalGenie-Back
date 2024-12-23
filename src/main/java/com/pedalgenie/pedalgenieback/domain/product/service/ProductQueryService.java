package com.pedalgenie.pedalgenieback.domain.product.service;

import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsRequest;
import com.pedalgenie.pedalgenieback.domain.category.dto.CategoryProductsResponse;
import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.category.repository.CategoryRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.FilterResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductsResponse;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductQueryRepositoryCustom;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
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
//    @Trace(resourceName = "상품 페이징 조회")
    public GetProductsResponse getProductsByFilters(FilterRequest filterDto){
        return GetProductsResponse.from(
                getPagingProducts(filterDto)
        );
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

        return GetProductResponse.of(product);
    }



}
