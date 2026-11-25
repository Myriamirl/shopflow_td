package com.shopflow.notification.service;

import com.shopflow.notification.dto.NotificationRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    private final List<Email> sent = new ArrayList<>();
    private final NotificationService service = new NotificationService(sent::add);

    @Test
    void sends_the_email_and_keeps_it_in_history() {
        service.send(new NotificationRequest("alice@shopflow.example", "Commande reçue", "Merci !"));

        assertThat(sent).hasSize(1);
        assertThat(sent.get(0).to()).isEqualTo("alice@shopflow.example");
        assertThat(service.history()).hasSize(1);
    }

    @Test
    void history_starts_with_the_most_recent_email() {
        service.send(new NotificationRequest("alice@shopflow.example", "Premier", "1"));
        service.send(new NotificationRequest("bob@shopflow.example", "Second", "2"));

        assertThat(service.history()).extracting(Email::subject).containsExactly("Second", "Premier");
    }
}
