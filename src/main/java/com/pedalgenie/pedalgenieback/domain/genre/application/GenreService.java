package com.pedalgenie.pedalgenieback.domain.genre.application;

import com.pedalgenie.pedalgenieback.domain.genre.dto.request.GenreRequestDto;
import com.pedalgenie.pedalgenieback.domain.genre.dto.response.GenreResponseDto;
import com.pedalgenie.pedalgenieback.domain.genre.entity.Genre;
import com.pedalgenie.pedalgenieback.domain.genre.entity.GenreProduct;
import com.pedalgenie.pedalgenieback.domain.genre.repository.GenreProductRepository;
import com.pedalgenie.pedalgenieback.domain.genre.repository.GenreRepository;
import com.pedalgenie.pedalgenieback.domain.product.application.ProductQueryService;
import com.pedalgenie.pedalgenieback.domain.product.dto.response.ProductResponse;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.productImage.application.ProductImageQueryService;
import com.pedalgenie.pedalgenieback.global.exception.CustomException;
import com.pedalgenie.pedalgenieback.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final ProductRepository productRepository;
    private final GenreProductRepository genreProductRepository;
    private final ProductImageQueryService productImageQueryService;
    private final ProductQueryService productQueryService;

    // 어드민 - 장르 생성
    @Transactional
    public GenreResponseDto createGenre(GenreRequestDto requestDto) {
        // Genre 생성
        Genre genre = Genre.builder()
                .genreName(requestDto.getGenreName())
                .build();

        Genre savedGenre = genreRepository.save(genre);

        // Genre - Product 연관 관계 설정
        List<Long> productIds = requestDto.getProductsIdList();

        if (productIds != null && !productIds.isEmpty()) {
            List<GenreProduct> genreProducts = productIds.stream()
                    .map(productId -> {
                        Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

                        return GenreProduct.builder()
                                .genre(savedGenre)
                                .product(product)
                                .build();
                    })
                    .toList();
            genreProductRepository.saveAll(genreProducts);
        }

        return GenreResponseDto.builder()
                .genreId(savedGenre.getGenreId())
                .genreName(savedGenre.getGenreName())
                .productsIdList(productIds)
                .build();
    }

    // 어드민 - 장르 전체 조회
    public List<GenreResponseDto> getAdminGenreList() {
        List<Genre> genres = genreRepository.findAll();

        return genres.stream()
                .map(genre -> {
                    List<Long> productIds = genreProductRepository.findByGenre(genre).stream()
                            .map(genreProduct -> genreProduct.getProduct().getId())
                            .collect(Collectors.toList());
                    return GenreResponseDto.builder()
                            .genreId(genre.getGenreId())
                            .genreName(genre.getGenreName())
                            .productsIdList(productIds)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 장르 악기 상세 조회
    public List<ProductResponse> getGenreProduct(String genreName, Long memberId){
        // 장르 조회
        Genre genre = genreRepository.findByGenreName(genreName)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_GENRE));

        // GenreProduct 조회
        List<GenreProduct> genreProducts = genreProductRepository.findByGenre(genre);

        if (genreProducts.isEmpty()) {
            return List.of();
        }

        // Product 조회
        List<Product> products = genreProducts.stream()
                .map(genreProduct -> genreProduct.getProduct())
                .collect(Collectors.toList());

        // 좋아요한 상품 ID 리스트 조회
        List<Long> likedProductIds = (memberId == null) ? List.of()
                : productQueryService.getLikedProductIds(memberId, products.stream().map(Product::getId).toList());

        return products.stream()
                .map(product -> ProductResponse.from(
                        product,
                        productImageQueryService.getFirstProductImage(product.getId()),
                        memberId == null ? null : likedProductIds.contains(product.getId()) // 로그인 여부에 따라 null 또는 true/false
                ))
                .toList();
    }
}

