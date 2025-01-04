package com.pedalgenie.pedalgenieback.domain.product.repository;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.product.dto.request.FilterRequest;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.QGetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.application.SortBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pedalgenie.pedalgenieback.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetProductQueryResponse> findPagingProducts(
            Category category,
            FilterRequest request){

        Boolean isRentable = request.isRentable();
        Boolean isPurchasable = request.isPurchasable();
        Boolean isDemoable = request.isDemoable();
        List<Long> subCateooryIds = request.subCategoryIds();

        return queryFactory.select(new QGetProductQueryResponse(
                product.id,
                product.name,
                product.shop.shopname,
                product.rentPricePerDay,
                product.isRentable,
                product.isPurchasable,
                product.isDemoable
                ))
                .from(product)
                .where(

                        inCategories(category),
                        isRentalbeFilter(isRentable),
                        isPurchasableFilter(isPurchasable),
                        isDemoableFilter(isDemoable),
                        inSubCategories(subCateooryIds)
                )
                .orderBy(getSorter(request.sortBy()))
                .fetch();

    }

    private BooleanExpression inCategories(Category category){

        if(category == null){
            return null;
        }
        return product.subCategory.category.eq(category);
    }


    private BooleanExpression isRentalbeFilter(Boolean isRentable) {
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

    private OrderSpecifier getSorter(SortBy sortBy){
        if (sortBy == SortBy.RECENT){
            return product.id.desc();
        }
        if (sortBy == SortBy.NAME_ASC){
            return product.name.asc();
        }
        // 상품 좋아요 구현 이후 정렬 기준 추가할 것

        return product.name.asc(); // 기본 정렬 이름순
    }




}
