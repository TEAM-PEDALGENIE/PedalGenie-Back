package com.pedalgenie.pedalgenieback.domain.product.application;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.like.application.LikeService;
import com.pedalgenie.pedalgenieback.domain.like.repository.ProductLikeRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.*;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductQueryRepositoryCustom;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.subcategory.dto.FilterSubCategoryResponse;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.domain.subcategory.repository.SubcategoryRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final LikeService likeService;
    private final ProductLikeRepository productLikeRepository;


    // 전체, 상위 카테고리별 목록 조회(필터, 정렬, 서브 카테고리 옵션)
    public List<GetProductQueryResponse> getProductsByCategory(
            Category category,
            FilterRequest request,
            Long memberId,
            Pageable pageable) {

        Page<GetProductQueryResponse> products =productQueryRepository
                .findPagingProducts(category, request, memberId, pageable);

        return products.stream()
                .map(product -> {
                    if (memberId == null) {
                        return new GetProductQueryResponse(
                                product.id(),
                                product.name(),
                                product.shopId(),
                                product.shopName(),
                                product.rentPricePerDay(),
                                product.isRentable(),
                                product.isPurchasable(),
                                product.isDemoable(),
                                product.imageUrl(),
                                null // 응답 DTO 에서 isLiked 제외
                        );
                    }
                    return product;
                })
                .toList();

    }

    // 이용 범위 옵션 조회
    public FilterResponse getMetadataForFilter() {
        return FilterResponse.of();
    }

    // 서브 카테고리 옵션 조회
    public FilterSubCategoryResponse getMetaForSubCategory(Category category) {
        List<SubCategory> subCategories = subcategoryRepository.findByCategory(category);
        return FilterSubCategoryResponse.of(subCategories);
    }

    // 정렬 옵션 조회
    public List<SortBy> getSortOptions() {
        return SortBy.getAllSortOptions();
    }


    // 상품 상세 조회
    public GetProductResponse getProductResponse(Long id, Long memberId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        List<ProductImageDto> productImages = productImageQueryService.getProductImages(id);

        // 로그인하지 않은 유저 처리
        Boolean isLiked = null;
        if(memberId!=null){
            isLiked = likeService.isProductLiked(id, memberId);
        }

        return GetProductResponse.of(product, productImages, isLiked);
    }

    // 매장에 속한 상품 목록 조회
    public List<ProductResponse> getProductsByShop(Long shopId, Long memberId) {

        List<Product> products = productRepository.findByShopId(shopId);

        // 좋아요한 상품 ID 리스트 조회
        List<Long> likedProductIds = (memberId == null) ? List.of() : getLikedProductIds(memberId, products.stream().map(Product::getId).toList());

        // 조회한 상품들을 ProductResponse로 변환하여 반환
        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    Boolean isLiked = likedProductIds.contains(product.getId()) // 상품이 좋아요된 상품인지 체크
                            ? true: false;

                    return ProductResponse.from(
                            product,
                            productImageQueryService.getFirstProductImage(product.getId()),
                            memberId!=null ? isLiked: null // 로그인한 유저만 필드 포함
                    );
                })
                .toList();

        return productResponses;
    }

    // 좋아요한 상품 id 목록 조회
    public List<Long> getLikedProductIds(Long memberId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return productLikeRepository.findLikedProductIdsByMember(memberId, productIds);
    }

}

