package com.app.beleza.respository;

import com.app.beleza.model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Boolean existsByToken(String token);

    Optional<PasswordReset> findByToken(String token);
}
