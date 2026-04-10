package com.prakashkbn2.Expense_Tracker.Security;

import com.prakashkbn2.Expense_Tracker.Entity.User;
import com.prakashkbn2.Expense_Tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * FILE : src/main/java/com/prakashkbn2/Expense_Tracker/Security/UserDetailsServiceImpl.java
 * ACTION: REPLACE existing file
 * CHANGE: loadUserByUsername now looks up by email (the "username" for Spring Security
 *         is the email address in this application)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        // Spring Security still calls this method "loadUserByUsername"
        // but we are passing the email as the identifier.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // ← email is the "username" principal
                user.getPassword(),
                Collections.emptyList()
        );
    }
}