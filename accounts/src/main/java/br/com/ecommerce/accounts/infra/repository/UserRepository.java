package br.com.ecommerce.accounts.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.accounts.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginUsername(String username);

	Boolean existsByLoginUsername(String username);

	@Query("SELECT COUNT(c) > 0 FROM UserClient c WHERE c.email.value = :email")
    boolean existsByEmailValue(String email);

	@Query("SELECT COUNT(c) > 0 FROM UserClient c WHERE c.cpf.value = :cpf")
    boolean existsByCpfValue(String cpf);

	@Query("SELECT COUNT(c) > 0 FROM UserClient c WHERE c.phone_number.value = :phoneNumber")
    boolean existsByPhoneNumberValue(String phoneNumber);
}