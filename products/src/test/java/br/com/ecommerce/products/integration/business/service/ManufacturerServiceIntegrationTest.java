package br.com.ecommerce.products.integration.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.api.dto.manufacturer.CreateManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataAddressDTO;
import br.com.ecommerce.products.api.dto.manufacturer.DataManufacturerDTO;
import br.com.ecommerce.products.api.dto.manufacturer.UpdateManufacturerDTO;
import br.com.ecommerce.products.api.mapper.AddressMapper;
import br.com.ecommerce.products.business.service.ManufacturerService;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.exception.exceptions.ManufacturerNotFoundException;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;
import br.com.ecommerce.products.utils.util.RandomUtils;

@ServiceIntegrationTest
class ManufacturerServiceIntegrationTest {

    private static List<Manufacturer> manufacturersPersisted;

    @Autowired
    private ManufacturerService service;
    @Autowired
    private PhoneUtils phoneUtils;
    @Autowired
    private RandomUtils randomUtils;
    @Autowired
    private AddressUtils addressUtils;
    @Autowired
    private AddressMapper addressMapper;

    @BeforeAll
    static void setup(
        @Autowired ManufacturerRepository repository,
        @Autowired ManufacturerUtils manufacturerUtils,
        @Autowired PhoneUtils phoneUtils,
        @Autowired AddressUtils addressUtils
    ) {
        manufacturersPersisted = Stream.generate(() -> {
                Phone phone = phoneUtils.getPhoneInstance();
                Address address = addressUtils.getAddressInstance();
                return manufacturerUtils.getManufacturerInstance(phone, address);
            })
            .limit(2)
            .toList();
        repository.saveAll(manufacturersPersisted);
    }

    @Rollback
    @Test
    @DisplayName("Integration - createManufacturer - Must return a DTO with the data of the created Manufacturer")
    void createManufacturerTest01() {
        // arrange
        String name = randomUtils.getRandomString(10);
        String phone = phoneUtils.getRandomPhoneString();
        String email = randomUtils.getRandomString(20);
        String contactPerson = randomUtils.getRandomString(30);
        Address addressObj = addressUtils.getAddressInstance();
        DataAddressDTO address = addressMapper.toDataAddressDTO(addressObj);
        var requestBody = new CreateManufacturerDTO(
            name, phone, email, contactPerson, address
        );
        
        // act
        DataManufacturerDTO result = service.createManufacturer(requestBody);

        // assert
        assertNotNull(result.getId());
        assertEquals(requestBody.getName().toUpperCase(), result.getName());
        assertEquals(requestBody.getPhone(), result.getPhone());
        assertEquals(requestBody.getEmail(), result.getEmail());
        assertEquals(requestBody.getContactPerson(), result.getContactPerson());
        assertEquals(requestBody.getAddress().toString(), result.getAddress().toString());
    }

    @Rollback
    @Test
    @DisplayName("Integration - createManufacturer - Should throw exception when name already exists")
    void createManufacturerTest02() {
        // arrange
        String existentName = manufacturersPersisted.get(0).getName();
        var requestBody = new CreateManufacturerDTO(
            existentName, null, null, null, null
        );
        
        // act and assert
        assertThrows(
            IllegalArgumentException.class,
            () -> service.createManufacturer(requestBody));
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateManufacturer - Must return a ManufacturerResponseDTO updated")
    void updateManufacturerTest01() {
        // arrange
        String newName = randomUtils.getRandomString(40);
        String newPhone = phoneUtils.getRandomPhoneString();
        String newEmail = randomUtils.getRandomString(50);
        String newContactPerson = randomUtils.getRandomString(60);
        Address addressObj = addressUtils.getAddressInstance();
        DataAddressDTO newAddress = addressMapper.toDataAddressDTO(addressObj);
        var requestBody = new UpdateManufacturerDTO(
            newName, newPhone, newEmail, newContactPerson, newAddress
        );

        // act
        DataManufacturerDTO result = service.updateManufacturer(1L, requestBody);

        // assert
        assertEquals(requestBody.getName().toUpperCase(), result.getName());
        assertEquals(requestBody.getPhone(), result.getPhone());
        assertEquals(requestBody.getEmail(), result.getEmail());
        assertEquals(requestBody.getContactPerson(), result.getContactPerson());
        assertEquals(requestBody.getAddress().toString(), result.getAddress().toString());
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateManufacturer - Should throw exception when name already exists")
    void updateManufacturerDataTest02() {
        String existentName = manufacturersPersisted.get(0).getName();
        assertThrows(
            IllegalArgumentException.class, 
            () -> service.updateManufacturer(
                1L, 
                new UpdateManufacturerDTO(existentName, null, null, null, null)));
    }

    @Rollback
    @Test
    @DisplayName("Integration - updateManufacturer - Should throw exception when not finding manufacturer")
    void updateManufacturerDataTest03() {
        Long unexistentId = 100000L;
        assertThrows(
            ManufacturerNotFoundException.class, 
            () -> service.updateManufacturer(unexistentId, new UpdateManufacturerDTO()));
    }
}