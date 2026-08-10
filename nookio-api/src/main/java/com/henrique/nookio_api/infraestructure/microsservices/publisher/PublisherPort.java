package com.henrique.nookio_api.infraestructure.microsservices.publisher;

public interface PublisherPort {

    void send(String email, String subject, String content);
}
