package com.java.Connectly.Contoller;

import com.java.Connectly.entities.User;
import com.java.Connectly.helper.Message;
import com.java.Connectly.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        String userName=principal.getName(); //get the username from session: in this case email
        User user= userRepository.getUserByEmail(userName);
        model.addAttribute("user", user);
        return "userPages/dashboard";
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