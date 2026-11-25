package com.shopflow.order.exception;

/** catalog-service ne répond pas ou répond en erreur : on ne peut pas vérifier les produits (HTTP 503). */
public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(Throwable cause) {
        super("Le catalogue est momentanément indisponible, réessayez plus tard", cause);
    }
}
