package com.shopflow.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Implémentation de TP : l'e-mail est écrit dans les logs au lieu d'être envoyé. */
@Slf4j
@Component
public class LoggingEmailSender implements EmailSender {

    @Override
    public void send(Email email) {
        log.info("E-mail envoyé à {} | {} | {}", email.to(), email.subject(), email.body());
    }
}
