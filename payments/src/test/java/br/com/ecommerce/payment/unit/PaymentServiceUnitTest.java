package br.com.ecommerce.payment.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecommerce.payment.model.Payment;
import br.com.ecommerce.payment.model.PaymentDTO;
import br.com.ecommerce.payment.model.PaymentStatus;
import br.com.ecommerce.payment.repository.PaymentRepository;
import br.com.ecommerce.payment.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @Mock
    private PaymentRepository repository;
    @InjectMocks
    private PaymentService service;


    @Test
    @DisplayName("Unit - createPayment - With valid PaymentDTO should not throw exception")
    void createPaymentTest01() {
        // act and assert
        assertDoesNotThrow( 
            () -> {
                service.createPayment(new PaymentDTO("1", "1", BigDecimal.TEN));
                verify(repository).save(any());
            });
    }

    @Test
    @DisplayName("Unit - createPayment - With negative amount should throw IllegalArgumentException")
    void createPaymentTest02() {
        // act and assert
        assertThrows(IllegalArgumentException.class, 
            () -> {
                service.createPayment(new PaymentDTO("1", "1", new BigDecimal("-10")));
                verifyNoInteractions(repository);
            });
    }

    @Test
    @DisplayName("Unit - createPayment - With null values in PaymentDTO should throw IllegalArgumentException")
    void createPaymentTest03() {
        // act and assert
        assertThrows(IllegalArgumentException.class,
            () -> {
                service.createPayment(new PaymentDTO(null, "1", BigDecimal.TEN));
                verifyNoInteractions(repository);
            });

        assertThrows(IllegalArgumentException.class,
            () -> {
                service.createPayment(new PaymentDTO("1", null, BigDecimal.TEN));
                verifyNoInteractions(repository);
            });

        assertThrows(IllegalArgumentException.class,
            () -> {
                service.createPayment(new PaymentDTO("1", "1", null));
                verifyNoInteractions(repository);
            });
    }

    @Test
    @DisplayName("Unit - confirmPayment - Successful confirmation for awaiting payment")
    void confirmPaymentTest01() {
        // arrange
        Payment payment = this.getPaymentMock(PaymentStatus.AWAITING);
        when(repository.findByOrderId(anyString())).thenReturn(Optional.of(payment));

        // act
        Payment result = service.confirmPayment("1");

        // assert
        assertEquals(payment, result);
    }

    @Test
    @DisplayName("Unit - confirmPayment - Throws exception for canceled payment")
    void confirmPaymentTest02() {
        // arrange
        Payment payment = this.getPaymentMock(PaymentStatus.CANCELED);
        when(repository.findByOrderId(anyString())).thenReturn(Optional.of(payment));

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> service.confirmPayment("1"));
    }

    @Test
    @DisplayName("Unit - cancelPayment - Successfully updates status to canceled")
    void cancelPaymentTest01() {
        // arrange
        Payment payment = this.getPaymentMock(PaymentStatus.AWAITING);
        when(repository.findByOrderId(anyString())).thenReturn(Optional.of(payment));
        
        // act
        Payment result = service.cancelPayment("1");

        // assert
        assertEquals(PaymentStatus.CANCELED, result.getStatus());
    }

    private Payment getPaymentMock(PaymentStatus status) {
        return Payment.builder()
            .orderId("1")
            .userId("1")
            .status(status)
            .paymentAmount(BigDecimal.TEN)
            .build();
    }
}
