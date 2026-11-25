package com.shopflow.payment.service;

import com.shopflow.payment.dto.PaymentRequest;
import com.shopflow.payment.dto.PaymentResponse;
import com.shopflow.payment.exception.PaymentAlreadyExistsException;
import com.shopflow.payment.exception.PaymentNotFoundException;
import com.shopflow.payment.model.Payment;
import com.shopflow.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Paiement simulé : accepté jusqu'à 1 000 EUR, refusé au-delà. */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    static final BigDecimal MAX_AMOUNT = new BigDecimal("1000.00");

    private final PaymentRepository payments;

    @Transactional
    public PaymentResponse pay(PaymentRequest request) {
        if (payments.existsByOrderId(request.orderId())) {
            throw new PaymentAlreadyExistsException(request.orderId());
        }

        BigDecimal amount = request.amount();
        Payment payment = amount.compareTo(MAX_AMOUNT) <= 0
                ? Payment.accepted(request.orderId(), request.customerId(), amount)
                : Payment.refused(request.orderId(), request.customerId(), amount,
                        "Paiement refusé : " + amount + " EUR dépasse le plafond de " + MAX_AMOUNT + " EUR");

        Payment saved = payments.save(payment);
        log.info("Paiement {} pour la commande {} ({} EUR)", saved.getStatus(), saved.getOrderId(), amount);
        return PaymentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findByOrderId(Long orderId) {
        return payments.findByOrderId(orderId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }
}
