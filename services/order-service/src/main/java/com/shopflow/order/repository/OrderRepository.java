package com.shopflow.order.repository;

import com.shopflow.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Charge la commande ET ses lignes en une seule requête (évite le problème N+1). */
    @EntityGraph(attributePaths = "lines")
    Optional<Order> findWithLinesById(Long id);

    /** Commandes d'un client, paginées. */
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
}
