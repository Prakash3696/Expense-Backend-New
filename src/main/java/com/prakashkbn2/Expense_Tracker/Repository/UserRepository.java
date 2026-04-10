package com.prakashkbn2.Expense_Tracker.Repository;

import com.prakashkbn2.Expense_Tracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Repository/UserRepository.java
 * ACTION: REPLACE existing file
 * CHANGE: findByUsername → findByEmail, existsByUsername → existsByEmail
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}