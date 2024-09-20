package br.com.ecommerce.orders.infra.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ecommerce.orders.infra.entity.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

	Optional<Order> findByIdAndUserId(String id, String userId);

	Page<Order> findAllByUserId(Pageable pageable, String userId);
}