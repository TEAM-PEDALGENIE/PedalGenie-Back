package com.pedalgenie.pedalgenieback.product;

import com.pedalgenie.pedalgenieback.domain.category.entity.Category;
import com.pedalgenie.pedalgenieback.domain.category.repository.CategoryRepository;
import com.pedalgenie.pedalgenieback.domain.product.entity.Product;
import com.pedalgenie.pedalgenieback.domain.product.repository.ProductRepository;
import com.pedalgenie.pedalgenieback.domain.shop.entity.Shop;
import com.pedalgenie.pedalgenieback.domain.shop.repository.ShopRepository;
import com.pedalgenie.pedalgenieback.domain.subcategory.entity.SubCategory;
import com.pedalgenie.pedalgenieback.domain.subcategory.repository.SubcategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;

    private SubCategory subCategory;
    private Category category;
    private Shop shop;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp(){
        category = Category.builder()
                .name("testCategory")
                .build();

        subCategory = SubCategory.builder()
                .name("testSubCategory")
                .category(category)
                .build();

        shop = Shop.builder()
                .name("testShop")
                .address("address")
                .contactNumber("01021254947")
                .demoQuantityPerDay(10)
                .build();

        product1 = Product.builder()
                .name("name1")
                .description("description1")
                .rentPricePerDay(1000.0)
                .rentQuantityPerDay(5)
                .subCategory(subCategory)
                .shop(shop)
                .build();

        product2 = Product.builder()
                .name("name2")
                .description("description2")
                .rentPricePerDay(1000.0)
                .rentQuantityPerDay(5)
                .subCategory(subCategory)
                .shop(shop)
                .build();

        product3 = Product.builder()
                .name("name3")
                .description("description3")
                .rentPricePerDay(1000.0)
                .rentQuantityPerDay(5)
                .subCategory(subCategory)
                .shop(shop)
                .build();

        // 테이블 간 저장 순서 유의
        categoryRepository.save(category);
        subcategoryRepository.save(subCategory);
        shopRepository.save(shop);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

    }

    @Test
    void 상품_조회_테스트(){
        // given & when
        final Product product1 = productRepository.findById(this.product1.getId())
                .orElse(null);
        final Product product2 = productRepository.findById(this.product2.getId())
                .orElse(null);
        final Product product3 = productRepository.findById(this.product3.getId())
                .orElse(null);

        assertSoftly(softly -> {
            softly.assertThat(product1).isEqualTo(this.product1);
            softly.assertThat(product2).isEqualTo(this.product2);
            softly.assertThat(product3).isEqualTo(this.product3);

        });
    }

    @Test
    void 카테고리에_해당하는_모든_상품을_조회한다(){
        //given & when
        final List<Product> productList = productRepository.findAllBySubCategoryCategoryId(category.getId());

        // then
        assertSoftly(softly ->{
            softly.assertThat(productList.size()).isEqualTo(3);
            softly.assertThat(productList.get(0)).isEqualTo(product1);
            softly.assertThat(productList.get(1)).isEqualTo(product2);
            softly.assertThat(productList.get(2)).isEqualTo(product3);

        });
    }

    @Test
    void 서브카테고리에_해당하는_모든_상품을_조회한다(){
        //given & when
        final List<Product> productList = productRepository.findBySubCategoryId(subCategory.getId());

        // then
        assertSoftly(softly ->{
            softly.assertThat(productList.size()).isEqualTo(3);
            softly.assertThat(productList.get(0)).isEqualTo(product1);
            softly.assertThat(productList.get(1)).isEqualTo(product2);
            softly.assertThat(productList.get(2)).isEqualTo(product3);

        });
    }

}
