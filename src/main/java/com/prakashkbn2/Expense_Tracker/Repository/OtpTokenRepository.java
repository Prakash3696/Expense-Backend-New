package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Repository/OtpTokenRepository.java
 * ACTION: CREATE (new file)
 */
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    /** Latest valid (not yet verified, not expired) OTP for an email */
    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);

    /** Bulk-delete all expired rows – call this on a schedule or before insert */
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.expiresAt < :now")
    void deleteAllExpired(LocalDateTime now);

    /** Remove all OTPs for an email once password is reset */
    @Modifying
    @Transactional
    void deleteAllByEmail(String email);
}