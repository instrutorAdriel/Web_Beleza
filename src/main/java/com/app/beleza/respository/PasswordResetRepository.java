package com.app.beleza.respository;

import com.app.beleza.model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Boolean existsByToken(String token);

    Optional<PasswordReset> findByToken(String token);
}
