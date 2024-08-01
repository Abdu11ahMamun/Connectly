package com.java.Connectly.Contoller;

import com.java.Connectly.entities.User;
import com.java.Connectly.entities.Contact;

import com.java.Connectly.helper.Message;
import com.java.Connectly.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/test")
//    @ResponseBody
//    public String test(){
//          User user= new User();
//          user.setName("Abdullah");
//          user.setEmail("a@gmail.com");
//
//          Contact  contact= new Contact();
//          user.getContacts().add(contact);
//          userRepository.save(user);
//
//          return "Saved";
//    }
    @RequestMapping(value = "/")
    public String helloWorld(Model model) {
        model.addAttribute("title","Home -Connectly for everyone");
        return "hello.html";
    }
    @RequestMapping(value = "/about")
    public String about(Model model) {
        model.addAttribute("title","About -Connectly for everyone");
        return "about";
    }

    @RequestMapping(value = "/signup")
    public String signup(Model model) {
        model.addAttribute("title", "SignUp - Connectly for everyone");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }

        try {
            if (!agreement) {
                throw new Exception("You have not agreed to the terms and conditions!");
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);

            User result = this.userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something Went Wrong!! " + e.getMessage(), "alert-error"));
            return "signup";
        }
    }





}
