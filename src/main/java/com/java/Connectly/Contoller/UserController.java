package com.java.Connectly.Contoller;

import com.java.Connectly.entities.Contact;
import com.java.Connectly.entities.User;
import com.java.Connectly.helper.Message;
import com.java.Connectly.repository.ContactRepository;
import com.java.Connectly.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

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
//                String uploadDir = "/Users/abdullahalmamun/SpringBootProjects/springBootUploadedImageDir";
                String uploadDir = new File("src/main/resources/static/img").getAbsolutePath();
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
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model model, Principal principal){
        model.addAttribute("title","Show User Contacts");
        String userEmail = principal.getName();
        User user = this.userRepository.getUserByEmail(userEmail);
        PageRequest pageRequest= PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findCountactsByUser(user.getId(),pageRequest);
        model.addAttribute("contacts",contacts);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages",contacts.getTotalPages());
        return "userPages/show_contacts";
    }

    @RequestMapping("contact/{cId}")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title","Show User Contacts");
        Optional<Contact> contactOptional = Optional.of(this.contactRepository.getById(cId));
        Contact contact = contactOptional.get();
        model.addAttribute("contact",contact);
        return "userPages/contact_details";
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