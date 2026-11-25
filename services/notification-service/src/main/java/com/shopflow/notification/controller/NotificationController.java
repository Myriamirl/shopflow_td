package com.shopflow.notification.controller;

import com.shopflow.notification.dto.NotificationRequest;
import com.shopflow.notification.service.Email;
import com.shopflow.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "E-mails envoyés aux clients ShopFlow")
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Envoyer un e-mail")
    public Email send(@Valid @RequestBody NotificationRequest request) {
        return service.send(request);
    }

    @GetMapping
    @Operation(summary = "Derniers e-mails envoyés")
    public List<Email> history() {
        return service.history();
    }
}
