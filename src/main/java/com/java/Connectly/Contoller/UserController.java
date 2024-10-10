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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
                String uploadDir = new File("build/resources/main/static/img").getAbsolutePath();
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

    @RequestMapping("/contact/{cId}")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "Show Contact Details");
        // Retrieve the contact by ID
        Optional<Contact> contactOptional = Optional.of(this.contactRepository.getById(cId));
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            String userName = principal.getName();
            User user = this.userRepository.getUserByEmail(userName);

            if (user.getId()==contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                return "userPages/contact_details";
            } else {
                session.setAttribute("message", new Message("You do not have permission to view this contact.", "alert-danger"));
                return "redirect:/user/show-contacts/0";
            }
        } else {
            session.setAttribute("message", new Message("Contact not found!", "alert-danger"));
            return "redirect:/user/show-contacts/0";
        }
    }

    @RequestMapping("/delete-contact/{cId}")
    public String deleteContact(@PathVariable("cId")Integer cId, Model model,Principal principal, HttpSession session){
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);

        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            String userName = principal.getName();
            User user = this.userRepository.getUserByEmail(userName);

            if (user.getId()==contact.getUser().getId()) {
                //model.addAttribute("contact", contact);
                String contactName = contact.getName();
                String contactId = String.valueOf(contact.getcId());
                String contactImageName = String.valueOf(contact.getImageURL());
                // Path where the images are stored
                String imageDir = new File("build/resources/main/static/img").getAbsolutePath();

                // Delete the image file associated with the contact
                if (contactImageName != null && !contactImageName.isEmpty()) {
                    File imageFile = new File(imageDir + File.separator + contactImageName);
                    if (imageFile.exists()) {
                        imageFile.delete();
                        System.out.println("Image deleted: " + contactImageName);
                    }
                }

                contact.setUser(null);
                this.contactRepository.delete(contact);
                session.setAttribute("message", new Message("Your contact " + contactName + " with ID " + contactId + " has been deleted", "alert-danger"));
                return "redirect:/user/show-contacts/0";
            } else {
                session.setAttribute("message", new Message("You do not have permission to view this contact.", "alert-danger"));
                return "redirect:/user/show-contacts/0";
            }
        } else {
            session.setAttribute("message", new Message("Contact not found!", "alert-danger"));
            return "redirect:/user/show-contacts/0";
        }

    }
    @PostMapping("/edit-contact/{cId}")
    public String editContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        // Fetch the contact from the database using cId, if needed
        Optional<Contact> contactOptional = contactRepository.findById(cId);

        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            model.addAttribute("contact", contact);
        } else {
            model.addAttribute("error", "Contact not found");
        }
        return "userPages/edit_contact_details";
    }
    @PostMapping("/update-contact")
    public String updateContact(@ModelAttribute Contact contact,
                                @RequestParam("imageFile") MultipartFile file,
                                Principal principal, HttpSession session) {
        try {
            // Retrieve the user who is saving the data
            String name = principal.getName();
            System.out.println("Principal Name: " + name);
            User user = this.userRepository.getUserByEmail(name);
            // System.out.println("User Retrieved: " + user);

            if (file.isEmpty()) {
                System.out.println("File is empty, no image uploaded.");
            } else if (contact.getImageURL()!=null && !contact.getImageURL().isEmpty()) {
                //if image exist and need to updated
                String imageDir = new File("build/resources/main/static/img").getAbsolutePath();
                    File imageFile = new File(imageDir + File.separator + contact.getImageURL());
                    if (imageFile.exists()) {
                        imageFile.delete();
                        System.out.println("Image deleted: " + contact.getImageURL());
                    }
                System.out.println("New image added");
                File saveDir = new File(imageDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded to: " + path);
                contact.setImageURL(fileName);

            } else {
                // Save the file to an external directory
                String uploadDir = new File("build/resources/main/static/img").getAbsolutePath();
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
            session.setAttribute("message", new Message("Successfully Updated Contact", "alert-success"));
            return "userPages/edit_contact_details";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-error"));
            return "userPages/edit_contact_details";
        }

    }
    @GetMapping("/profile")
    public String getProfile(){
        return "userPages/profile_details";
    }

    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable("id") Integer id, Model model) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "User not found");
        }
        return "userPages/update_user_details";
    }
    @PostMapping("/update-user")
    public String updateUserDetails(@ModelAttribute User user,
                                @RequestParam("imageFile") MultipartFile file,
                                Principal principal, HttpSession session) {
        try {
            // Retrieve the user who is saving the data
//            String name = principal.getName();
//            System.out.println("Principal Name: " + name);
//            User user = this.userRepository.getUserByEmail(name);
//             System.out.println("User Retrieved: " + user);

            if (file.isEmpty()) {
                System.out.println("File is empty, no image uploaded.");
            } else if (user.getImageUrl()!=null && !user.getImageUrl().isEmpty()) {
                //if image exist and need to updated
                String imageDir = new File("build/resources/main/static/img").getAbsolutePath();
                File imageFile = new File(imageDir + File.separator + user.getImageUrl());
                if (imageFile.exists()) {
                    imageFile.delete();
                    System.out.println("Image deleted: " + user.getImageUrl());
                }
                System.out.println("New image added");
                File saveDir = new File(imageDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded to: " + path);
                user.setImageUrl(fileName);

            } else {
                // Save the file to an external directory
                String uploadDir = new File("build/resources/main/static/img").getAbsolutePath();
                File saveDir = new File(uploadDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                String fileName = file.getOriginalFilename();
                Path path = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image uploaded to: " + path);
                user.setImageUrl(fileName);
            }
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                user.setPassword(user.getPassword());
            } else {
                User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception("User not found"));
                user.setPassword(existingUser.getPassword());
            }
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Successfully Updated Contact", "alert-success"));
            return "userPages/update_user_details";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-error"));
            return "userPages/update_user_details";
        }

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