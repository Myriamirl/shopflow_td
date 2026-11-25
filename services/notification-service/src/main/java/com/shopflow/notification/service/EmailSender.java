package com.shopflow.notification.service;

/**
 * Envoi d'e-mails. Une interface permet de remplacer l'implémentation
 * (journalisation en TP, vrai serveur SMTP en production) sans toucher au reste.
 */
public interface EmailSender {

    void send(Email email);
}
