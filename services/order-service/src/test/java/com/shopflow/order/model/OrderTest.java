package com.shopflow.order.model;

import com.shopflow.order.exception.IllegalOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests du domaine pur : aucune base, aucun Spring. */
class OrderTest {

    @Test
    void new_order_is_pending_with_zero_total() {
        Order order = Order.pending("c-1");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotal()).isEqualByComparingTo("0");
    }

    @Test
    void addLine_computes_total() {
        Order order = Order.pending("c-1");

        order.addLine(1L, "Clavier", new BigDecimal("89.90"), 2);
        order.addLine(2L, "Souris", new BigDecimal("49.90"), 1);

        assertThat(order.getLines()).hasSize(2);
        assertThat(order.getTotal()).isEqualByComparingTo("229.70");
    }

    @Test
    void same_product_twice_is_merged_into_one_line() {
        Order order = Order.pending("c-1");

        order.addLine(1L, "Clavier", new BigDecimal("89.90"), 1);
        order.addLine(1L, "Clavier", new BigDecimal("89.90"), 2);

        assertThat(order.getLines()).hasSize(1);
        assertThat(order.getLines().get(0).getQuantity()).isEqualTo(3);
        assertThat(order.getTotal()).isEqualByComparingTo("269.70");
    }

    @Test
    void cancelled_order_cannot_be_cancelled_again() {
        Order order = Order.pending("c-1");
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThatThrownBy(order::cancel).isInstanceOf(IllegalOrderStateException.class);
    }

    @Test
    void lines_cannot_be_modified_from_outside() {
        Order order = Order.pending("c-1");

        assertThatThrownBy(() -> order.getLines().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
