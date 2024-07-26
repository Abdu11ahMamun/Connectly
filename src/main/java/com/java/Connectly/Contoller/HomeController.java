package com.java.Connectly.Contoller;

import com.java.Connectly.entities.User;
import com.java.Connectly.entities.Contact;

import com.java.Connectly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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





}
