package com.shopflow.payment.service;

import com.shopflow.payment.dto.PaymentRequest;
import com.shopflow.payment.dto.PaymentResponse;
import com.shopflow.payment.exception.PaymentAlreadyExistsException;
import com.shopflow.payment.model.Payment;
import com.shopflow.payment.model.PaymentStatus;
import com.shopflow.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository payments;

    @InjectMocks
    PaymentService service;

    private static PaymentRequest request(long orderId, String amount) {
        return new PaymentRequest(orderId, "c-1", new BigDecimal(amount));
    }

    @Test
    void amount_up_to_the_limit_is_accepted() {
        when(payments.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = service.pay(request(1L, "1000.00"));

        assertThat(response.status()).isEqualTo(PaymentStatus.ACCEPTED);
        assertThat(response.reason()).isNull();
    }

    @Test
    void amount_above_the_limit_is_refused() {
        when(payments.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = service.pay(request(2L, "1199.00"));

        assertThat(response.status()).isEqualTo(PaymentStatus.REFUSED);
        assertThat(response.reason()).contains("plafond");
    }

    @Test
    void an_order_cannot_be_paid_twice() {
        when(payments.existsByOrderId(3L)).thenReturn(true);

        assertThatThrownBy(() -> service.pay(request(3L, "50.00")))
                .isInstanceOf(PaymentAlreadyExistsException.class);
        verify(payments, never()).save(any());
    }
}
