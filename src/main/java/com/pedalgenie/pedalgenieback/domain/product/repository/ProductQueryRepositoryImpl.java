package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.QGetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public List<GetProductQueryResponse> findPagingProducts( // 메서드 분리
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
            likeCondition.and(productLike.member.memberId.isNull());
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
                        productLike.productLikeId.max().isNotNull()


                ))
                .from(product)
                .leftJoin(productImage).on(productImage.product.id.eq(product.id))
                .leftJoin(productLike).on(productLike.product.id.eq(product.id)
                                .and(likeCondition))

                .where(

                        inCategories(category),
                        inSubCategories(subCategoryIds),
                        filterOptions(isRentable, isPurchasable,isDemoable)
                )
                .groupBy(product.id)
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
        // 상품 좋아요 구현 이후 정렬 기준 추가할 것

        return product.id.desc();// 기본 정렬 최신순
    }




}
