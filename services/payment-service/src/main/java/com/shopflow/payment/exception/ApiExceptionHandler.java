package com.shopflow.payment.exception;

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

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handleNotFound(PaymentNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Paiement introuvable", "payment-not-found", ex.getMessage());
    }

    @ExceptionHandler(PaymentAlreadyExistsException.class)
    public ProblemDetail handleAlreadyPaid(PaymentAlreadyExistsException ex) {
        return problem(HttpStatus.CONFLICT, "Commande déjà payée", "payment-already-exists", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Requête invalide", "validation-error",
                "Un ou plusieurs champs sont invalides");
        Map<String, String> errors = new LinkedHashMap<>();
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
