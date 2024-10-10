package com.java.Connectly.Contoller;

import com.java.Connectly.entities.Contact;
import com.java.Connectly.entities.User;
import com.java.Connectly.repository.ContactRepository;
import com.java.Connectly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> seach(@PathVariable("query") String query, Principal principal){
        System.out.println(query);
        User user = this.userRepository.getUserByEmail(principal.getName());
        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
}
