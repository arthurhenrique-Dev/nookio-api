package com.henrique.nookio_api.shared.emails.event;

public record SendEmailEvent(
        String email,
        String subject,
        String content
) {
}
