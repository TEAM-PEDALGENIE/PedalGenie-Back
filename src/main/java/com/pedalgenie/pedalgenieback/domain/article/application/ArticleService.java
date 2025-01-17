package com.pedalgenie.pedalgenieback.domain.article.application;

import com.pedalgenie.pedalgenieback.domain.article.dto.response.ArticleProductResponseDto;
import com.pedalgenie.pedalgenieback.domain.article.dto.response.ArticleResponseDto;
import com.pedalgenie.pedalgenieback.domain.article.entity.Article;
import com.pedalgenie.pedalgenieback.domain.article.entity.ArticleProduct;
import com.pedalgenie.pedalgenieback.domain.article.repository.ArticleProductRepository;
import com.pedalgenie.pedalgenieback.domain.article.repository.ArticleRepository;
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
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleProductRepository articleProductRepository;
    private final ProductRepository productRepository;
    private final ProductQueryService productQueryService;
    private final ProductImageQueryService productImageQueryService;

    // 어드민 - 아티클 생성 메서드
    @Transactional
    public ArticleResponseDto createArticle(String title, String hashTag,List<Long> productIds,
                                          String thumbnailUrl, String bodyUrl) {
        // Article 엔티티 생성
        Article article = Article.builder()
                .title(title)
                .hashTag(hashTag)
                .thumbnailUrl(thumbnailUrl)
                .bodyUrl(bodyUrl)
                .build();

        // 저장
        Article savedArticle = articleRepository.save(article);

        // article - product 연관 관계 설정
        if (productIds != null && !productIds.isEmpty()) {
            // ArticleProduct 리스트 생성
            List<ArticleProduct> articleProducts = productIds.stream()
                    .map(productId -> {
                        Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
                        return ArticleProduct.builder()
                                .article(savedArticle)
                                .product(product)
                                .build();
                    })
                    .toList();
            // 배치 저장
            articleProductRepository.saveAll(articleProducts);
        }


        // hashTag String을 List<String>으로 변환
        List<String> hashTagList = parseHashTags(hashTag);

        return ArticleResponseDto.builder()
                .articleId(savedArticle.getArticleId())
                .title(savedArticle.getTitle())
                .hashTag(hashTagList)
                .productIds(productIds)
                .thumbnailUrl(savedArticle.getThumbnailUrl())
                .bodyUrl(savedArticle.getBodyUrl())
                .build();
    }

    // 어드민 - 아티클 목록 조회 메서드
    public List<ArticleResponseDto> getAdminArticleList() {
        // 모든 아티클 조회
        List<Article> articles = articleRepository.findAll();

        return articles.stream()
                .map(article -> ArticleResponseDto.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    // 어드민 - 아티클 삭제 메서드
    @Transactional
    public void deleteArticle(Long articleId){
        // 아티클 조회
        articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE));

        // ArticleProduct (N) 먼저 삭제
        articleProductRepository.deleteByArticleId(articleId);
        // 그 후에 Article (1) 삭제
        articleRepository.deleteById(articleId);
    }

    // 아티클 상세 조회 메서드
    public ArticleResponseDto getArticle(Long articleId, Long memberId){
        // 아티클 조회
        Article article = articleRepository.findById(articleId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ARTICLE));

        // 연결된 상품 조회
        List<ArticleProduct> articleProducts = articleProductRepository.findByArticle(article);

        List<Product> products = articleProducts.stream()
                .map(ArticleProduct::getProduct)
                .toList();

        // 좋아요한 상품 ID 리스트 조회
        List<Long> likedProductIds = (memberId == null) ? List.of()
                : productQueryService.getLikedProductIds(memberId, products.stream().map(Product::getId).toList());

        // ArticleProductResponseDto로 변환
        List<ArticleProductResponseDto> productResponses = products.stream()
                .map(product -> {
                    Boolean likedState = null;
                    if (memberId != null) { // 로그인한 경우만 t/f로 반환
                        likedState = likedProductIds.contains(product.getId());
                    }
                    return ArticleProductResponseDto.from(
                            product,
                            productImageQueryService.getFirstProductImage(product.getId()).imageUrl(),
                            likedState
                    );
                })
                .toList();

        // 해시태그 변환
        List<String> hashTagList = parseHashTags(article.getHashTag());

        return ArticleResponseDto.builder()
                .articleId(article.getArticleId())
                .title(article.getTitle())
                .hashTag(hashTagList)
                .thumbnailUrl(article.getThumbnailUrl())
                .bodyUrl(article.getBodyUrl())
                .products(productResponses)
                .build();
    }


    // 아티클 목록 조회 메서드
    public List<ArticleResponseDto> getArticleList() {
        // 모든 아티클 조회
        List<Article> articles = articleRepository.findAll();

        return articles.stream()
                .map(article -> ArticleResponseDto.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .hashTag(parseHashTags(article.getHashTag()))
                        .thumbnailUrl(article.getThumbnailUrl())
                        .build())
                .collect(Collectors.toList());
    }

    // 해시태그를 List<String>으로 변환하는 메서드
    private List<String> parseHashTags(String hashTag) {
        if (hashTag != null && !hashTag.isEmpty()) {
            return List.of(hashTag.split(","))
                    .stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
