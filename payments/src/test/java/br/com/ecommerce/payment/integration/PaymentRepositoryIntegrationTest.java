package br.com.ecommerce.payment.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import br.com.ecommerce.payment.model.Payment;
import br.com.ecommerce.payment.model.PaymentStatus;
import br.com.ecommerce.payment.repository.PaymentRepository;
import br.com.ecommerce.payment.testcontainers.MySQLTestContainerConfig;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MySQLTestContainerConfig.class)
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository repository;

    @BeforeAll
    static void setup(@Autowired PaymentRepository repository) {
        repository.saveAll(
            List.of(
                Payment.builder().orderId(300L).userId(300L).paymentAmount(new BigDecimal("5000")).status(PaymentStatus.CONFIRMED).build(),

                Payment.builder().orderId(1L).build(),
                Payment.builder().orderId(1L).build(),
                Payment.builder().orderId(2L).build(),

                Payment.builder().userId(1L).build(),
                Payment.builder().userId(1L).build(),
                Payment.builder().userId(2L).build(),

                Payment.builder().paymentAmount(new BigDecimal("1")).build(),
                Payment.builder().paymentAmount(new BigDecimal("1")).build(),
                Payment.builder().paymentAmount(new BigDecimal("2")).build(),
                
                Payment.builder().status(PaymentStatus.AWAITING).build(),
                Payment.builder().status(PaymentStatus.AWAITING).build(),

                Payment.builder().userId(100L).paymentAmount(new BigDecimal("1000")).status(PaymentStatus.CANCELED).build(),
                Payment.builder().userId(100L).paymentAmount(new BigDecimal("1000")).status(PaymentStatus.CANCELED).build(),
                Payment.builder().userId(100L).paymentAmount(new BigDecimal("1000")).status(PaymentStatus.CONFIRMED).build(),
                Payment.builder().userId(100L).paymentAmount(new BigDecimal("2000")).status(PaymentStatus.CANCELED).build(),

                Payment.builder().userId(200L).paymentAmount(new BigDecimal("1000")).status(PaymentStatus.CANCELED).build(),
                Payment.builder().userId(300L).paymentAmount(new BigDecimal("3000")).status(PaymentStatus.CANCELED).build()
            )
        );
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by ID")
    void findAllByParamsTest01() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), 1L, null, null, null, null)
            .getContent();

        var payment = result.get(0);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("5000.00"), payment.getPaymentAmount());
        assertEquals(PaymentStatus.CONFIRMED, payment.getStatus());
        assertEquals(300L, payment.getUserId());
        assertEquals(300L, payment.getOrderId());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by orderId")
    void findAllByParamsTest02() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), null, 1L, null, null, null)
            .getContent();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by userId")
    void findAllByParamsTest03() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), null, null, 1L, null, null)
            .getContent();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by paymentAmount")
    void findAllByParamsTest04() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), null, null, null, new BigDecimal("1"), null)
            .getContent();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by status")
    void findAllByParamsTest05() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), null, null, null, null, PaymentStatus.AWAITING)
            .getContent();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Should find 2 records by userId, paymentAmount and status")
    void findAllByParamsTest06() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), null, null, 100L, new BigDecimal("1000"), PaymentStatus.CANCELED)
            .getContent();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - findAllByParams - Must find a record with all parameters")
    void findAllByParamsTest07() {
        var result = repository.findAllByParams(PageRequest.of(0, 35), 1L, 300L, 300L, new BigDecimal("5000"), PaymentStatus.CONFIRMED)
            .getContent();

        assertEquals(1, result.size());
    }
}