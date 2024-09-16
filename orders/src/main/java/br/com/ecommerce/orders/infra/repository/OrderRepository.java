package br.com.ecommerce.orders.infra.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.orders.infra.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByIdAndUserId(Long id, Long userId);

	Page<Order> findAllByUserId(Pageable pageable, Long userId);
}