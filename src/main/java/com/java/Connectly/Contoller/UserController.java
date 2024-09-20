package com.java.Connectly.Contoller;

import com.java.Connectly.entities.Contact;
import com.java.Connectly.entities.User;
import com.java.Connectly.helper.Message;
import com.java.Connectly.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String userName=principal.getName(); //get the username from session: in this case email
        User user= userRepository.getUserByEmail(userName);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model){
        return "userPages/dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContact(Model model){
        model.addAttribute("contact", new Contact());
        return "userPages/addContactForm";
    }
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("imageFile") MultipartFile file,
                                 Principal principal, HttpSession session) {
        System.out.println("Entered processContact method");
        try {
            // Retrieve the user who is saving the data
            String name = principal.getName();
            System.out.println("Principal Name: " + name);
            User user = this.userRepository.getUserByEmail(name);
           // System.out.println("User Retrieved: " + user);

            if (file.isEmpty()) {
                System.out.println("File is empty, no image uploaded.");
            } else {
                // Save the file to an external directory
                String uploadDir = "/Users/abdullahalmamun/SpringBootProjects/springBootUploadedImageDir";
                File saveDir = new File(uploadDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded to: " + path);
                contact.setImageURL(fileName);
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
            return "userPages/addContactForm";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-error"));
            return "userPages/addContactForm";
        }
    }
    @GetMapping("/show-contacts")
    public String showContacts(Model m){
        return "userPages/show_contacts";
    }
}

/*
* +-----------------------+     +-----------------------+
|  User Login Form       | --> |  AuthenticationManager |
+-----------------------+     +-----------------------+
                                    |
                                    v
                         +-----------------------+
                         | DaoAuthenticationProvider |
                         +-----------------------+
                                    |
                                    v
                         +-----------------------+
                         |  UserDetailsService    |
                         +-----------------------+
                                    |
                                    v
                         +-----------------------+
                         |    UserDetails (Principal)  |
                         +-----------------------+
                                    |
                                    v
                        +-------------------------+
                        |    SecurityContext      |
                        +-------------------------+
                                    |
                                    v
                         +-----------------------+
                         |     Controller          |
                         |  (Using Principal)      |
                         +-----------------------+

*
*
* */