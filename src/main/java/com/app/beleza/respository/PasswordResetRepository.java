package com.app.beleza.respository;

import com.app.beleza.model.PasswordReset;
import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
    Boolean existsByToken(String token);

    Optional<PasswordReset> findByToken(String token);

    // NOVO: tokens do usuário que ainda não expiraram (o mais recente primeiro)
    @Query("SELECT p FROM PasswordReset p " +
            "WHERE p.usuario = :usuario " +
            "AND p.data_expiracao > :agora " +
            "ORDER BY p.data_expiracao DESC")
    List<PasswordReset> buscarTokensValidos(@Param("usuario") Usuario usuario,
                                            @Param("agora") LocalDateTime agora);
}