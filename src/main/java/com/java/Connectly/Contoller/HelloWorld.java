package com.java.Connectly.Contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorld {
    @RequestMapping(value = "/HelloWorld")
    public String helloWorld() {
        return "hello.html";
    }
}
