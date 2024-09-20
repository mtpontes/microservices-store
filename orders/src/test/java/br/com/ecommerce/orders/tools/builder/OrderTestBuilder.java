package br.com.ecommerce.orders.tools.builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.orders.infra.entity.Order;
import br.com.ecommerce.orders.infra.entity.OrderStatus;
import br.com.ecommerce.orders.infra.entity.Product;

public class OrderTestBuilder {

    private String id;
    private String userId;
    private List<Product> products = new ArrayList<>();
    private BigDecimal total;
    private LocalDate date;
    private OrderStatus status;


    public OrderTestBuilder id(String id) {
        this.id = id;
        return this;
    }

    public OrderTestBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public OrderTestBuilder products(List<Product> products) {
        this.products.addAll(products);
        return this;
    }

    public OrderTestBuilder total(BigDecimal total) {
        this.total = total;
        return this;
    }

    public OrderTestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public OrderTestBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public Order build() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", this.id);
        ReflectionTestUtils.setField(order, "userId", this.userId);
        ReflectionTestUtils.setField(order, "products", this.products);
        ReflectionTestUtils.setField(order, "total", this.total);
        ReflectionTestUtils.setField(order, "date", Optional.ofNullable(this.date).orElse(LocalDate.now().plusDays(1)));
        ReflectionTestUtils.setField(order, "status", this.status);
        return order;
    }
}