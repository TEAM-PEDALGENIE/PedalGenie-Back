package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.QGetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pedalgenie.pedalgenieback.domain.like.entity.QProductLike.productLike;
import static com.pedalgenie.pedalgenieback.domain.product.entity.QProduct.product;
import static com.pedalgenie.pedalgenieback.domain.productImage.QProductImage.productImage;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetProductQueryResponse> findPagingProducts(
            Category category,
            FilterRequest request,
            Long memberId) {

        Boolean isRentable = request.isRentable();
        Boolean isPurchasable = request.isPurchasable();
        Boolean isDemoable = request.isDemoable();
        List<Long> subCategoryIds = request.subCategoryIds();

        // memberId 가 null 일 때 처리
        BooleanBuilder likeCondition = new BooleanBuilder();
        if (memberId != null) {
            likeCondition.and(productLike.member.memberId.eq(memberId));
        } else {
            // null 을 반환하지 않도록 모든 사용자의 좋아요를 조회
            likeCondition.and(productLike.member.memberId.isNotNull());
        }

        return queryFactory.select(new QGetProductQueryResponse(
                product.id,
                product.name,
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
                .groupBy(product.id, productLike.productLikeId) // 중복 발생 가능
                .orderBy(getSorter(request.sortBy()))
                .fetch();

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




}
