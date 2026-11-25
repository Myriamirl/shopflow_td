package com.shopflow.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/** Traduit les exceptions en réponses ProblemDetail (RFC 7807) avec le bon code HTTP. */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String TYPE_BASE = "https://shopflow.example/problems/";

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleNotFound(OrderNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Commande introuvable", "order-not-found", ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ProblemDetail handleInvalidOrder(InvalidOrderException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Commande invalide", "invalid-order", ex.getMessage());
    }

    @ExceptionHandler(IllegalOrderStateException.class)
    public ProblemDetail handleIllegalState(IllegalOrderStateException ex) {
        return problem(HttpStatus.CONFLICT, "Opération impossible", "illegal-order-state", ex.getMessage());
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    public ProblemDetail handleCatalogDown(CatalogUnavailableException ex) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Catalogue indisponible", "catalog-unavailable", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Requête invalide", "validation-error",
                "Un ou plusieurs champs sont invalides");
        Map<String, String> errors = new LinkedHashMap<>();
        // Les champs imbriqués apparaissent sous la forme lines[0].quantity
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        pd.setProperty("errors", errors);
        return pd;
    }

    private ProblemDetail problem(HttpStatus status, String title, String type, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(TYPE_BASE + type));
        return pd;
    }
}
