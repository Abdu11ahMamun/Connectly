package com.java.Connectly.Config;

import com.java.Connectly.entities.User;
import com.java.Connectly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImp {
    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String email) {
        // Fetching user from database
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            throw new RuntimeException("Could not found user");
        }

        return user;
    }
}
