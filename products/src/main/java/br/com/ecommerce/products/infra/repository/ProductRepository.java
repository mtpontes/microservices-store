package br.com.ecommerce.products.infra.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.products.infra.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query("""
            SELECT p from Product p WHERE
            (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:categoryName IS NULL OR LOWER(p.category.name) = LOWER(:categoryName))
            AND (:minPrice IS NULL OR p.price.currentPrice >= :minPrice)
            AND (:maxPrice IS NULL OR p.price.currentPrice <= :maxPrice)
            AND (:manufacturerName IS NULL OR LOWER(p.manufacturer.name) = LOWER(:manufacturerName))
        """)
    Page<Product> findAllByParams(
        String name,
        String categoryName,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String manufacturerName,
        Pageable pageable
    );

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.endOfPromotion BETWEEN :now AND :oneHourLater")
    Set<Product> findAllOnPromotionEndingWithinNextHour(LocalDateTime now, LocalDateTime oneHourLater);

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.endOfPromotion < CURRENT_TIMESTAMP")
    Set<Product> findAllWithExpiredPromotions();
}