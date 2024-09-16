package br.com.ecommerce.products.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import br.com.ecommerce.products.utils.util.AddressUtils;
import br.com.ecommerce.products.utils.util.CategoryUtils;
import br.com.ecommerce.products.utils.util.DepartmentUtils;
import br.com.ecommerce.products.utils.util.ManufacturerUtils;
import br.com.ecommerce.products.utils.util.PhoneUtils;
import br.com.ecommerce.products.utils.util.PriceUtils;
import br.com.ecommerce.products.utils.util.ProductUtils;
import br.com.ecommerce.products.utils.util.RandomUtils;
import br.com.ecommerce.products.utils.util.StockUtils;

@TestConfiguration
public class TestConfigBeans {

    @Bean
    public AddressUtils getAddressUtils() {
        return new AddressUtils();
    }

    @Bean
    public CategoryUtils getCategoryUtils() {
        return new CategoryUtils();
    }

    @Bean
    public DepartmentUtils getDepartmentUtils() {
        return new DepartmentUtils();
    }

    @Bean
    public ManufacturerUtils getManufacturerUtils() {
        return new ManufacturerUtils();
    }

    @Bean
    public PhoneUtils getPhoneUtils() {
        return new PhoneUtils();
    }

    @Bean
    public PriceUtils getPriceUtils() {
        return new PriceUtils();
    }

    @Bean
    public ProductUtils getProductUtils() {
        return new ProductUtils();
    }


    @Bean
    public StockUtils getStockUtils() {
        return new StockUtils();
    }

    @Bean
    public RandomUtils getRandomUtils() {
        return new RandomUtils();
    }
}