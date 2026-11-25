package com.shopflow.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/** E-mail à envoyer. */
@Builder
public record NotificationRequest(
        @NotBlank(message = "Le destinataire est obligatoire")
        @Email(message = "Le destinataire doit être une adresse e-mail")
        String to,

        @NotBlank(message = "Le sujet est obligatoire")
        String subject,

        @NotBlank(message = "Le message est obligatoire")
        String body
) {
}
