package com.java.Connectly.Config;

import com.java.Connectly.entities.User;
import com.java.Connectly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(username);

        if (user == null) {
            throw new RuntimeException("Could not found user");
        }
        CustomUserDetails customUserDetails= new CustomUserDetails(user);
        return customUserDetails;
    }
}
