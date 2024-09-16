package br.com.ecommerce.products.unit.infra.entity.manufacturer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;

class ManufacturerTest {

    private final String name = "name";
    private final String email = "email";
    private final String contactPerson = "person";
    private final Address address = new AddressUtils().getAddressInstance();
    private final Phone phone = new PhoneUtils().getPhoneInstance();
    private Manufacturer manufacturer;


    @BeforeEach
    void setup() {
        this.manufacturer = new Manufacturer(name, phone, email, contactPerson, address);
    }


    @Test
    void createManufacturer_withValidParams() {
        assertDoesNotThrow(() -> {
            var result = new Manufacturer(name, phone, email, contactPerson, address);

            assertNotEquals(name, result.getName());
            assertEquals(name.toUpperCase(), result.getName());
        });        
    }

    @Test
    void createManufacturer_withNullParams() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Manufacturer(null, phone, email, contactPerson, address));        
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Manufacturer(name, null, email, contactPerson, address));              
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Manufacturer(name, phone, email, contactPerson, null));              
    }

    @Test
    void createManufacturer_withBlankStrings() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> new Manufacturer("", phone, email, contactPerson, address));        
    }

    @Test
    void testUpdate_withValidValues() {
        final Manufacturer target = manufacturer;
        final String updateString = "update";
        final Address updateAddress = address;
        ReflectionTestUtils.setField(updateAddress, "street", "street updated");
        final Phone phoneUpdate = phone;
        ReflectionTestUtils.setField(phoneUpdate, "value", "+55 47 99784-5151");

        assertDoesNotThrow(() -> {
            target.update(updateString, phone, email, contactPerson, address);
            assertTrue(target.getName().equalsIgnoreCase(updateString));
        });

        assertDoesNotThrow(() -> {
            target.update(name, phoneUpdate, email, contactPerson, address);
            assertEquals(phoneUpdate.getValue(), target.getPhone());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, updateString, contactPerson, address);
            assertEquals(updateString, target.getEmail());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, email, updateString, address);
            assertEquals(updateString, target.getContactPerson());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, email, contactPerson, updateAddress);
            assertEquals(updateAddress.getStreet(), target.getAddress().getStreet());
        });
    }

    @Test
    void testUpdate_withNullParams() {
        final Manufacturer target = manufacturer;

        assertDoesNotThrow(() -> {
            target.update(null, phone, email, contactPerson, address);
            assertNotNull(target.getName());
        });

        assertDoesNotThrow(() -> {
            target.update(name, null, email, contactPerson, address);
            assertNotNull(target.getEmail());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, null, contactPerson, address);
            assertNotNull(target.getEmail());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, email, null, address);
            assertNotNull(target.getContactPerson());
        });

        assertDoesNotThrow(() -> {
            target.update(name, phone, email, contactPerson, null);
            assertNotNull(target.getContactPerson());
        });
    }

    @Test
    void testUpdate_withBlankStrings() {
        assertDoesNotThrow(() -> {
            final Manufacturer target = manufacturer;
            target.update("", phone, email, contactPerson, address);

            assertNotEquals("", target.getName());
        });
                
        assertDoesNotThrow(() -> {
            final Manufacturer target = manufacturer;
            target.update(name, phone, "", contactPerson, address);

            assertNotEquals("", target.getEmail());
        });

        assertDoesNotThrow(() -> {
            final Manufacturer target = manufacturer;
            target.update(name, phone, email, "", address);

            assertNotEquals("", target.getContactPerson());
        });       
    }
}