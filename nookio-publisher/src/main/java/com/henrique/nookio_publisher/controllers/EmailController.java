package com.henrique.nookio_publisher.controllers;

import com.henrique.nookio_publisher.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_publisher.services.SesEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EmailController {

    private final SesEmailService sesEmailService;

    @AuditLog(resource = "PUBLISHER_EMAIL", operation = "SEND")
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(
            @RequestParam("email") String email,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content
    ) {
        sesEmailService.sendEmail(email, subject, content);
        return ResponseEntity.ok().build();
    }
}
