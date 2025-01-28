package com.pedalgenie.pedalgenieback.domain.like.application;

import com.pedalgenie.pedalgenieback.domain.like.dto.LikedShopDto;
import com.pedalgenie.pedalgenieback.domain.like.entity.ProductLike;
import com.pedalgenie.pedalgenieback.domain.like.entity.ShopLike;
import com.pedalgenie.pedalgenieback.domain.like.repository.ProductLikeRepository;
import com.pedalgenie.pedalgenieback.domain.like.repository.ShopLikeRepository;
import com.pedalgenie.pedalgenieback.domain.member.entity.Member;
import com.pedalgenie.pedalgenieback.domain.member.repository.MemberRepository;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.GetProductQueryResponse;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.domain.productImage.application.dto.ProductImageDto;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Tag(name = "Like api", description = "악기, 가게 좋아요 생성, 삭제, 조회 기능을 포함합니다.")
public class LikeService {

    private final ProductLikeRepository productLikeRepository;
    private final ShopLikeRepository shopLikeRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final ProductImageQueryService productImageQueryService;


    // 상품 좋아요 생성
    @Transactional
    public void createProductLike(Long productId, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        // 좋아요하려는 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 이미 좋아요를 눌렀는지 확인
        boolean isAlreadyLiked = productLikeRepository.existsByProductAndMember(product, member);
        if (isAlreadyLiked) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // 좋아요 저장
        productLikeRepository.save(ProductLike.builder()
                .product(product)
                .member(member)
                .build());
    }

    // 가게 좋아요 생성
    @Transactional
    public void createShopLike(Long shopId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));

        // 이미 좋아요를 눌렀는지 확인
        boolean isAlreadyLiked = shopLikeRepository.existsByShopAndMember(shop, member);
        if (isAlreadyLiked) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // 좋아요 저장
        shopLikeRepository.save(ShopLike.builder()
                .shop(shop)
                .member(member)
                .build());
    }

    // 상품 좋아요 삭제
    @Transactional
    public void removeProductLike(Long productId, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 해당 상품에 멤버가 누른 좋아요 조회
        ProductLike productLike = productLikeRepository.findByProductAndMember(product, member)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LIKE));

        // 좋아요 삭제
        productLikeRepository.delete(productLike);
    }

    // 가게 좋아요 삭제
    @Transactional
    public void removeShopLike(Long shopId, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        // 가게 조회
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));

        // 해당 가게에 멤버가 누른 좋아요 조회
        ShopLike shopLike = shopLikeRepository.findByShopAndMember(shop, member)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LIKE));

        // 좋아요 삭제
        shopLikeRepository.delete(shopLike);
    }

    // 좋아요한 상품 목록 조회
    public List<ProductResponse> getProductLikeList(Long memberId) {
        List<Product> likedProducts = productLikeRepository.findLikedProductsByMember(memberId);

        if (likedProducts.isEmpty()) {
            return List.of();
        }

        return likedProducts.stream()
                .map(product -> {
                    Boolean isLiked = true; // 이미 좋아요된 상품이므로 항상 true
                    return ProductResponse.from(
                            product,
                            productImageQueryService.getFirstProductImage(product.getId()),
                            isLiked
                    );
                })
                .toList();
    }


    // 좋아요한 가게 목록 조회
    public List<LikedShopDto> getShopLikeList(Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));
        // 좋아요한 가게 목록 조회
        List<Shop> likedShops = shopLikeRepository.findLikedShopsByMember(member);

        return likedShops.stream()
                .map(shop -> LikedShopDto.builder()
                        .shopId(shop.getId())
                        .shopName(shop.getShopname())
                        .thumbNailUrl(shop.getImageUrl())
                        .isLiked(true)
                        .build())
                .collect(Collectors.toList());
    }

    // 특정 상품 좋아요 여부 조회
    public boolean isProductLiked(Long productId, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        // 좋아요 여부 확인
        return productLikeRepository.existsByProductAndMember(product, member);
    }


    // 특정 가게 좋아요 여부 조회
    public boolean isShopLiked(Long shopId, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXISTS_MEMBER_ID));

        // 가게 조회
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SHOP_NAME));

        // 좋아요 여부 확인
        return shopLikeRepository.existsByShopAndMember(shop, member);
    }



    // 상품 좋아요 개수 조회
    public Long countProductLikes(Long productId) {
        return productLikeRepository.countByProductId(productId);
    }

    // 가게 좋아요 개수 조회
    public Long countShopLikes(Long shopId) {
        return shopLikeRepository.countByShopId(shopId);
    }

    // 특정 유저의 상품 좋아요 삭제
    @Transactional
    public void deleteProductLikesByMemberId(Long memberId) {
        productLikeRepository.deleteByMemberId(memberId);
    }

    // 특정 유저의 상점 좋아요 삭제
    @Transactional
    public void deleteShopLikesByMemberId(Long memberId) {
        shopLikeRepository.deleteByMemberId(memberId);
    }

}
