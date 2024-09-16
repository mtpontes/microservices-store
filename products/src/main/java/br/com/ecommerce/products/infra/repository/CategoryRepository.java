package br.com.ecommerce.products.infra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.products.infra.entity.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Boolean existsByName(String name);

    @Query("""
            SELECT c FROM Category c WHERE 
            (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name ,'%')))
            """)
    public Page<Category> findAllByParams(String name, Pageable pageable);
}