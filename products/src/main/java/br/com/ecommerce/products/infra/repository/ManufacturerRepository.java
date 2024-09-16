package br.com.ecommerce.products.infra.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long>{

	Optional<Manufacturer> findByName(String manufacturerName);

	@Query("""
			SELECT m FROM Manufacturer m WHERE
			(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) 
			AND (:phone IS NULL OR m.phone.value = :phone)
			AND (:email IS NULL OR m.email = :email)
			AND (:contactPerson IS NULL OR LOWER(m.contactPerson) 
				LIKE LOWER(CONCAT('%', :contactPerson,'%')))
			""")
	Page<Manufacturer> findAllByParams(
		String name,
		String phone,
		String email,
		String contactPerson,
		Pageable pageable
	);

	@Query("""
			SELECT m FROM Manufacturer m WHERE
			(:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
			AND (:contactPerson IS NULL OR LOWER(m.contactPerson) 
				LIKE LOWER(CONCAT('%', :contactPerson,'%')))
			""")
	Page<Manufacturer> findAllByParams(String name, String contactPerson, Pageable pageable);

	boolean existsByName(String name);
}