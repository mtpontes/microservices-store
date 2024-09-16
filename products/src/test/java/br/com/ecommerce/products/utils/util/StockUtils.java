package br.com.ecommerce.products.utils.util;

import java.util.Random;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.infra.entity.product.Stock;

@TestComponent
public class StockUtils {

    private Random random = new Random();


    public Stock getStockInstance() {
        Integer units = this.getRandomQuantity();
        Stock stock = new Stock();
        ReflectionTestUtils.setField(stock, "unit", units);
        return stock;
    }

    private int getRandomQuantity() {
        return random.nextInt(100) + 1;
    }
}