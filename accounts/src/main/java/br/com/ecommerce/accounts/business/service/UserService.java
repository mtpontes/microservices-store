package br.com.ecommerce.accounts.business.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.ecommerce.accounts.api.dto.CreateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.CreateUserEmployeeDTO;
import br.com.ecommerce.accounts.api.dto.DataUserClientDTO;
import br.com.ecommerce.accounts.api.dto.DataUserDTO;
import br.com.ecommerce.accounts.api.dto.UpdateUserClientDTO;
import br.com.ecommerce.accounts.api.dto.UserEmployeeCreatedDTO;
import br.com.ecommerce.accounts.api.factory.EmailFactory;
import br.com.ecommerce.accounts.api.factory.PhoneNumberFactory;
import br.com.ecommerce.accounts.api.factory.UserFactory;
import br.com.ecommerce.accounts.api.mapper.AddressMapper;
import br.com.ecommerce.accounts.infra.repository.UserRepository;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClient;
import br.com.ecommerce.accounts.model.valueobjects.Address;
import br.com.ecommerce.accounts.model.valueobjects.Email;
import br.com.ecommerce.accounts.model.valueobjects.PhoneNumber;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

	private UserRepository repository;
	private AddressMapper addressMapper;
	private UserFactory factory;
    private PhoneNumberFactory phoneNumberFactory;
    private EmailFactory emailFactory;;


	@Transactional
	public DataUserClientDTO saveClientUser(CreateUserClientDTO dto) {
		UserClient user = factory.createClient(
			dto.getUsername(), 
			dto.getPassword(), 
			dto.getName(),
			dto.getEmail(),
			dto.getPhone_number(), 
			dto.getCpf(), 
			addressMapper.toAddress(dto.getAddress()));

		repository.save(user);
		return new DataUserClientDTO(
			user.getId(), user.getName(), user.getEmail(), user.getPhone_number(), user.getCPF(), addressMapper.toAddressDTO(user.getAddress()));
	}

	@Transactional
	public UserEmployeeCreatedDTO saveEmployeeUser(CreateUserEmployeeDTO dto) {
		User user = factory.createEmployee(
			dto.getUsername(), dto.getPassword(), dto.getName());
				
		repository.save(user);
		return new UserEmployeeCreatedDTO(user);
	}

	public DataUserDTO getCurrentUserData(Long id) {
		return repository.findById(id)
			.map(user -> new DataUserDTO(user.getId(), user.getName(), user.getUsername()))
			.orElseThrow(EntityNotFoundException::new);
	}

	public DataUserClientDTO getCurrentUserClientData(Long id) {
		return repository.findById(id)
			.map(user -> (UserClient) user)
			.map(user -> new DataUserClientDTO(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getPhone_number(),
				user.getCPF(),
				addressMapper.toAddressDTO(user.getAddress())
			))
			.orElseThrow(EntityNotFoundException::new);
	}

	@Transactional
    public DataUserClientDTO updateUserClient(UpdateUserClientDTO dto, Long userId) {
		UserClient user = (UserClient) this.repository.findById(userId)
			.orElseThrow(EntityNotFoundException::new);
			
		PhoneNumber phoneVo = Optional.ofNullable(dto.getPhone_number())
			.filter(p -> !p.isBlank())
			.map(p -> phoneNumberFactory.createPhoneNumber(p))
			.orElse(null);
		
		Email emailVo = Optional.ofNullable(dto.getEmail())
			.filter(e -> !e.isBlank())
			.map(e -> emailFactory.createEmail(e))
			.orElse(null);

		Address addressVo;
		try {
			addressVo = addressMapper.toAddress(dto.getAddress());
			
		} catch (IllegalArgumentException ex) {
			addressVo = null;
		}
		user.update(emailVo, phoneVo, addressVo);

		return new DataUserClientDTO(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getPhone_number(),
			user.getCPF(),
			addressMapper.toAddressDTO(user.getAddress())
		);
    }
}