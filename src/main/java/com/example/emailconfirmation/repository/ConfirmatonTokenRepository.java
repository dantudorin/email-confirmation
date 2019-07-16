package com.example.emailconfirmation.repository;

import com.example.emailconfirmation.models.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfirmatonTokenRepository extends JpaRepository<ConfirmationToken,String> {
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
}
