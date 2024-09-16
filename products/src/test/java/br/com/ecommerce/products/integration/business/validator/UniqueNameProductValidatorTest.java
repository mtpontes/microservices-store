package br.com.ecommerce.products.integration.business.validator;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.annotations.ServiceIntegrationTest;
import br.com.ecommerce.products.business.validator.UniqueNameProductValidator;
import br.com.ecommerce.products.infra.entity.category.Category;
import br.com.ecommerce.products.infra.entity.department.Department;
import br.com.ecommerce.products.infra.entity.manufacturer.Address;
import br.com.ecommerce.products.infra.entity.manufacturer.Manufacturer;
import br.com.ecommerce.products.infra.entity.manufacturer.Phone;
import br.com.ecommerce.products.infra.entity.product.Price;
import br.com.ecommerce.products.infra.entity.product.Product;
import br.com.ecommerce.products.infra.entity.product.Stock;
import br.com.ecommerce.products.infra.repository.CategoryRepository;
import br.com.ecommerce.products.infra.repository.DepartmentRepository;
import br.com.ecommerce.products.infra.repository.ManufacturerRepository;
import br.com.ecommerce.products.infra.repository.ProductRepository;
import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;
import br.com.ecommerce.products.utils.util.PriceUtils;
import br.com.ecommerce.products.utils.util.ProductUtils;
import br.com.ecommerce.products.utils.util.StockUtils;

@ServiceIntegrationTest
class UniqueNameProductValidatorTest {

    @Autowired
    private UniqueNameProductValidator validator;
    static private String existentName = "product 1";

    @BeforeAll
    static void setup(
        @Autowired DepartmentRepository departmentRepository,
        @Autowired CategoryRepository categoryRepository,
        @Autowired ManufacturerRepository manufacturerRepository,
        @Autowired ProductRepository productRepository,
        @Autowired DepartmentUtils departmentUtils,
        @Autowired CategoryUtils categoryUtils,
        @Autowired AddressUtils addressUtils,
        @Autowired PhoneUtils phoneUtils,
        @Autowired ManufacturerUtils manufacturerUtils,
        @Autowired PriceUtils priceUtils,
        @Autowired StockUtils stockUtils,
        @Autowired ProductUtils productUtils
    ) {
        Department department = departmentUtils.getDepartmentInstance();
        departmentRepository.save(department);

        Category category = categoryUtils.getCategoryInstance(department);
        categoryRepository.save(category);
        department.addCategory(category);
        departmentRepository.save(department);

        Phone phone = phoneUtils.getPhoneInstance();
        Address address = addressUtils.getAddressInstance();
        Manufacturer manufacturer = manufacturerUtils.getManufacturerInstance(phone, address);
        manufacturerRepository.save(manufacturer);
        
        Price price = priceUtils.getPriceInstance();
        Stock stock = stockUtils.getStockInstance();
        Product product = productUtils.getProductInstance(price, stock, category, manufacturer);
        ReflectionTestUtils.setField(product, "name", existentName);
        manufacturer.addProduct(product);
        productRepository.save(product);
        manufacturerRepository.save(manufacturer);
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