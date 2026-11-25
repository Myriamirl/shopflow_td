package com.shopflow.notification.service;

import java.time.Instant;

/** Un e-mail envoyé. */
public record Email(String to, String subject, String body, Instant sentAt) {
}
