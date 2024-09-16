package br.com.ecommerce.accounts.infra.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.ecommerce.accounts.api.factory.UserFactory;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.User;
import jakarta.annotation.PostConstruct;

@Service
public class UserAdminCreation {

	@Value("${user.admin.username}")
	private String username;
	@Value("${user.admin.password}")
	private String password;

	@Autowired
	private UserRepository rp;
	@Autowired
	private UserFactory factory;
	
	@PostConstruct
	public void createUserAdmin() {
		if(!rp.existsByLoginUsername(username)) {
			User userAdmin = factory
				.createAdmin(username, password, "default admin");
			rp.save(userAdmin);
		}
	}
}