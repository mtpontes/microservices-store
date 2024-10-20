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

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.startPromotion BETWEEN :init AND :end")
    Set<Product> findAllByPromotionStartingBetween(LocalDateTime init, LocalDateTime end);

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.endPromotion BETWEEN :init AND :hoursLater")
    Set<Product> findAllOnPromotionEndingBetween(LocalDateTime init, LocalDateTime hoursLater);

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.endPromotion < :now")
    Set<Product> findAllExpiredPromotions(LocalDateTime now);

    @Query("SELECT p FROM Product p WHERE p.price.onPromotion = true AND p.price.endPromotion > :targetDate")
    Set<Product> findAllByEndOfPromotionIsAfterOf(LocalDateTime targetDate);
}