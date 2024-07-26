package com.java.Connectly.Contoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorld {
    @RequestMapping(value = "/HelloWorld")
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
        model.addAttribute("title","SignUp -Connectly for everyone");
        return "signup";
    }


}
