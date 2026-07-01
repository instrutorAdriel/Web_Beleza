package com.example.demo.respository;

import com.example.demo.model.PasswordReset;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Boolean existsByToken(String token);

    Optional<PasswordReset> findByToken(String token);
}
