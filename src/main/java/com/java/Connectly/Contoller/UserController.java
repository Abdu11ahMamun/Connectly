package com.java.Connectly.Contoller;

import com.java.Connectly.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
public class UserController {
    @RequestMapping(value = "/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard - Connectly for everyone");
        model.addAttribute("user", new User());
        return "dashboard";
    }
}
