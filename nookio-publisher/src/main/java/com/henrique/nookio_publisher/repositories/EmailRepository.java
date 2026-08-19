package com.henrique.nookio_publisher.repositories;

import com.henrique.nookio_publisher.models.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailRepository extends JpaRepository<EmailModel, UUID> {
    List<EmailModel> findByStatus(String status);
}
