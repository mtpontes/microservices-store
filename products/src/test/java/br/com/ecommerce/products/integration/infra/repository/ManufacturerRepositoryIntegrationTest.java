package br.com.ecommerce.products.integration.infra.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.annotations.RepositoryIntegrationTest;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;

@RepositoryIntegrationTest
class ManufacturerRepositoryIntegrationTest {

    private final Pageable pageable = Pageable.unpaged();

    @Autowired
    private ManufacturerRepository repository;

    @Autowired 
    private ManufacturerUtils manufacturerUtils;
    @Autowired 
    private ManufacturerRepository manufacturerRepository;

    @BeforeAll
    static void setup(
        @Autowired PhoneUtils phoneUtils,
        @Autowired ManufacturerUtils manufacturerUtils,
        @Autowired ManufacturerRepository manufacturerRepository
    ) {
        IntStream.range(0, 4)
            .mapToObj(flux -> {
                Phone phone = phoneUtils.getPhoneInstance();
                Manufacturer manufacturer = manufacturerUtils.getManufacturerInstance(phone);
                return manufacturerRepository.save(manufacturer);
            })
            .collect(Collectors.toList());
    }


    @Test
    @DisplayName("Integration - findAllByParams - Should return all manufacturers when parameters are null")
    void findAllByParamsTest01() {
        // act
        var sizeResult = repository.findAllByParams(null, null, pageable)
            .getContent()
            .size();

        // asser
        assertEquals(repository.count(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all manufacturers with name like 'name' parameter")
    void findAllByParamsTest02() {
        // arrange
        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer1, "name", "a name");
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer2, "name", "a name 2");
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        String nameParam = "name";
        int sizeResult = repository.findAllByParams(nameParam, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all manufacturers with name like 'name' parameter")
    void findAllByParamsTest03() {
        // arrange
        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer1, "contactPerson", "Fulano");
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer2, "contactPerson", "Fulano-san");
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        String contactPerson = "Fulano";
        int sizeResult = repository.findAllByParams(null, contactPerson, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should return all manufacturers when parameters are null")
    void findAllByParamsTest04() {
        // act
        var sizeResult = repository.findAllByParams(null, null, null, null, pageable)
            .getContent()
            .size();
        
        // assert
        assertEquals(repository.count(), sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return correct manufacturers by param 'name'")
    void findAllByParamsTest05() {
        // arrange
        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer1, "name", "a name");
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer2, "name", "a name 2");
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        String nameParam = "name";
        int sizeResult = repository.findAllByParams(
            nameParam, null, null, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return correct manufacturers by param 'phone'")
    void findAllByParamsTest06() {
        // arrange
        String phoneString = "+55 47 98520-9258";
        Phone phone = new Phone();
        ReflectionTestUtils.setField(phone, "value", phoneString);

        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance(phone);
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance(phone);
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        int sizeResult = repository.findAllByParams(
            null, phoneString, null, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must return correct manufacturers by param 'email'")
    void findAllByParamsTest07() {
        // arrange
        String email = "integration@email.com";
        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer1, "email", email);
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer2, "email", email);
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        int sizeResult = repository.findAllByParams(
            null, null, email, null, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }

    @Test
    @DisplayName("Integration - findAllByParams - It should return all manufacturers with name like 'name' parameter")
    void findAllByParamsTest08() {
        // arrange
        String contactPerson = "Sr. Fulano";
        Manufacturer manufacturer1 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer1, "contactPerson", contactPerson);
        Manufacturer manufacturer2 = manufacturerUtils.getManufacturerInstance();
        ReflectionTestUtils.setField(manufacturer2, "contactPerson", contactPerson);
        manufacturerRepository.saveAll(List.of(manufacturer1, manufacturer2));

        // act
        String likeContactPerson = "Sr. F";
        int sizeResult = repository.findAllByParams(
            null, null, null, likeContactPerson, pageable)
            .getContent()
            .size();

        // assert
        assertEquals(2, sizeResult);
    }
}