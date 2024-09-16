package br.com.ecommerce.products.integration.business.validator;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.business.validator.UniqueNameManufacturerValidator;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;

@ServiceIntegrationTest
class UniqueNameManufacturerValidatorTest {

    @Autowired
    private UniqueNameManufacturerValidator validator;
    static private String existentName = "manufacturer 1".toUpperCase();

    @BeforeAll
    static void setup(
        @Autowired ManufacturerRepository repository,
        @Autowired CategoryRepository staticRepository,
        @Autowired PhoneUtils phoneUtils,
        @Autowired AddressUtils addressUtils,
        @Autowired ManufacturerUtils manufacturerUtils
    ) {
        Phone phone = phoneUtils.getPhoneInstance();
        Address address = addressUtils.getAddressInstance();
        Manufacturer manufacturer = manufacturerUtils.getManufacturerInstance(phone, address);
        ReflectionTestUtils.setField(manufacturer, "name", existentName);
        repository.save(manufacturer);
    }


    @Test
    void testValidator_withExistentNames() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(existentName));
    }

    @Test
    void testValidator_withNonExistentName() {
        assertDoesNotThrow(() -> validator.validate("random"));
    }
}