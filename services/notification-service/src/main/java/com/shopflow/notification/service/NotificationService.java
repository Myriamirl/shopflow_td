package com.shopflow.notification.service;

import com.shopflow.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Envoie un e-mail et garde en mémoire les derniers envois pour pouvoir les consulter.
 * Pas de base de données : l'historique est perdu au redémarrage, ce qui suffit ici.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final int HISTORY_SIZE = 100;

    private final EmailSender sender;
    private final List<Email> history = Collections.synchronizedList(new ArrayList<>());

    public Email send(NotificationRequest request) {
        Email email = new Email(request.to(), request.subject(), request.body(), Instant.now());
        sender.send(email);
        history.add(0, email);
        if (history.size() > HISTORY_SIZE) {
            history.remove(history.size() - 1);
        }
        return email;
    }

    /** Derniers e-mails envoyés, du plus récent au plus ancien. */
    public List<Email> history() {
        synchronized (history) {
            return List.copyOf(history);
        }
    }
}
