package br.com.ecommerce.products.business.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataAddressDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.SimpleDataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.UpdateManufacturerDTO;
import br.com.ecommerce.products.api.mapper.AddressMapper;
import br.com.ecommerce.products.api.mapper.ManufacturerMapper;
import br.com.ecommerce.products.business.validator.UniqueNameManufacturerValidator;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.tools.factory.PhoneFactory;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ManufacturerService {

	private final ManufacturerRepository repository;

	private final PhoneFactory phoneFactory;
	private final ManufacturerMapper manufacturerMapper;
	private final AddressMapper addressMapper;

	private final UniqueNameManufacturerValidator uniqueNameValidator;


	public Page<SimpleDataManufacturerDTO> getAllSimpleDataManufacturers(
		String name,
		String contactPerson,
		Pageable pageable
	) {
		return repository.findAllByParams(name, contactPerson, pageable)
			.map(manufacturerMapper::toSimpleDataManufacturerDTO);
	}

	public Page<DataManufacturerDTO> getAllManufacturers(
		String name, 
		String phone, 
		String email, 
		String contactPerson, 
		Pageable pageable
	) {
		return repository.findAllByParams(
			name,
			phone,
			email,
			contactPerson,
			pageable)
			.map(manufacturer -> {
				DataAddressDTO address = addressMapper.toDataAddressDTO(manufacturer.getAddress());
				return manufacturerMapper.toDataManufacturerDTO(manufacturer, address);
			});
	}

	public DataManufacturerDTO getManufacturer(Long id) {
		return repository.findById(id)
			.map(manufacturer -> {
				DataAddressDTO address = addressMapper.toDataAddressDTO(manufacturer.getAddress());
				return manufacturerMapper.toDataManufacturerDTO(manufacturer, address);
			})
			.orElseThrow(EntityNotFoundException::new);
	}

	@Transactional
	public DataManufacturerDTO createManufacturer(CreateManufacturerDTO dto) {
		uniqueNameValidator.validate(dto.getName());
		Address address = addressMapper.toAddress(dto.getAddress());
		Manufacturer manufacturer = manufacturerMapper.toManufacturer(dto, address);
		repository.save(manufacturer);
		DataAddressDTO addressData = addressMapper.toDataAddressDTO(manufacturer.getAddress());
		return manufacturerMapper.toDataManufacturerDTO(manufacturer, addressData);
	}

	@Transactional
	public DataManufacturerDTO updateManufacturer(Long id, UpdateManufacturerDTO dto) {
		uniqueNameValidator.validate(dto.getName());
		return repository.findById(id)
			.map(manufacturer -> {
				Phone newPhone = Optional.ofNullable(dto.getPhone())
					.map(p -> phoneFactory.createPhone(p))
					.orElse(null);
				Address address = Optional.ofNullable(dto.getAddress())
					.map(a -> addressMapper.toAddress(a))
					.orElse(null);
					
				manufacturer.update(
					dto.getName(), 
					newPhone, 
					dto.getEmail(),
					dto.getContactPerson(),
					address);
				return repository.save(manufacturer);
			})
			.map(manufacturer -> {
				DataAddressDTO address = addressMapper.toDataAddressDTO(manufacturer.getAddress());
				return manufacturerMapper.toDataManufacturerDTO(manufacturer, address);
			})
			.orElseThrow(EntityNotFoundException::new);
	}
}