package br.com.ecommerce.products.infra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.products.infra.entity.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    public Boolean existsByName(String name);

    @Query("""
            SELECT d FROM Department d WHERE 
            (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
            """)
    public Page<Department> findAllByParams(String name,  Pageable pageable);
}