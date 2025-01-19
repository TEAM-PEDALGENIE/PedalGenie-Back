package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.QGetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.domain.subcategory.repository.SubcategoryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pedalgenie.pedalgenieback.domain.like.entity.QProductLike.productLike;
import static com.pedalgenie.pedalgenieback.domain.product.entity.QProduct.product;
import static com.pedalgenie.pedalgenieback.domain.productImage.QProductImage.productImage;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final SubcategoryRepository subcategoryRepository;

    @Override
    public Page<GetProductQueryResponse> findPagingProducts(
            Category category,
            FilterRequest request,
            Long memberId,
            Pageable pageable) {

        Boolean isRentable = request.isRentable();
        Boolean isPurchasable = request.isPurchasable();
        Boolean isDemoable = request.isDemoable();
        List<String> subCategoryNames = request.subCategoryNames();

        // 서브카테고리 이름으로 id 조회
        List<Long> subCategoryIds = getSubCategoryIdsByNames(category, subCategoryNames);


        // memberId 가 null 일 때 처리
        BooleanBuilder likeCondition = new BooleanBuilder();
        if (memberId != null) {
            likeCondition.and(productLike.member.memberId.eq(memberId));
        } else {
            // null 을 반환하지 않도록 모든 사용자의 좋아요를 조회
            likeCondition.and(productLike.member.memberId.isNotNull());
        }

        List<GetProductQueryResponse> content = queryFactory
                .select(new QGetProductQueryResponse(
                product.id,
                product.name,
                product.shop.id,
                product.shop.shopname,
                product.rentPricePerDay,
                product.isRentable,
                product.isPurchasable,
                product.isDemoable,

                        productImage.imageUrl.stringValue().min(),
                        productLike.productLikeId.isNotNull() // 집계함수 사용 불가


                ))
                .distinct() // 중복 결과 제거
                .from(product)
                .leftJoin(productImage).on(productImage.product.id.eq(product.id))
                .leftJoin(productLike).on(productLike.product.id.eq(product.id)
                                .and(likeCondition))

                .where(

                        inCategories(category),
                        inSubCategories(subCategoryIds),
                        filterOptions(isRentable, isPurchasable,isDemoable)
                )
                .offset(pageable.getOffset()) // 몇 번째 페이지부터 시작할 것인지
                .limit(pageable.getPageSize()) // 페이지당 몇 개의 데이터를 보여줄 것인지
                .groupBy(product.id, productLike.productLikeId) // 중복 발생 가능
                .orderBy(getSorter(request.sortBy()))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.id.countDistinct()) // distinct한 product.id 개수를 세도록 수정
                .from(product)
                .leftJoin(productImage).on(productImage.product.id.eq(product.id))
                .leftJoin(productLike).on(productLike.product.id.eq(product.id)
                        .and(likeCondition))
                .where(
                        inCategories(category),
                        inSubCategories(subCategoryIds),
                        filterOptions(isRentable, isPurchasable, isDemoable)
                );


        return PageableExecutionUtils.getPage(content, pageable,
                countQuery::fetchOne);

    }

    private BooleanExpression inCategories(Category category){

        if(category == null){
            return null;
        }
        return product.subCategory.category.eq(category);
    }

    // 이용 범위 옵션 AND 연산
    public BooleanBuilder filterOptions(Boolean isRentable, Boolean isPurchasable, Boolean isDemoable){

        return new BooleanBuilder()
                .and(isRentableFilter(isRentable))
                .and(isPurchasableFilter(isPurchasable))
                .and(isDemoableFilter(isDemoable));
    }

    private BooleanExpression isRentableFilter(Boolean isRentable) {
        if (isRentable == null) {
            return null;
        }
        return product.isRentable.eq(isRentable);
    }

    private BooleanExpression isPurchasableFilter(Boolean isPurchasable) {
        if (isPurchasable == null) {
            return null;
        }
        return product.isPurchasable.eq(isPurchasable);
    }


    private BooleanExpression isDemoableFilter(Boolean isDemoable) {
        if (isDemoable == null) {
            return null;
        }
        return product.isDemoable.eq(isDemoable);
    }


    private BooleanExpression inSubCategories(List<Long> subCategoryIds){
        if (subCategoryIds == null || subCategoryIds.isEmpty()){
            return null;
        }
        return product.subCategory.id.in(subCategoryIds);
    }

    private OrderSpecifier<?> getSorter(SortBy sortBy){
        if (sortBy == SortBy.RECENT){
            return product.id.desc();
        }
        if (sortBy == SortBy.NAME_ASC){
            return product.name.asc();
        }
        if (sortBy == SortBy.LIKE_DESC){
            return product.productLikes.size().desc();
        }

        return product.id.desc();// 기본 정렬 최신순
    }
    // SubCategoryName을 사용하여 SubCategory ID 조회
    private List<Long> getSubCategoryIdsByNames(Category category, List<String> subCategoryNames) {
        if (subCategoryNames == null || subCategoryNames.isEmpty()) {
            return List.of();
        }
        List<SubCategory> subCategories = subcategoryRepository.findByCategory(category);
        return subCategories.stream()
                .filter(subCategory -> subCategoryNames.contains(subCategory.getName()))
                .map(SubCategory::getId) // 이름과 매칭되는 id 추출
                .toList();
    }




}
