package com.markerhub.controller;

import com.markerhub.base.annotation.Login;
import com.markerhub.entity.User;
import com.markerhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestController {

    @Autowired
    UserRepository userRepository;

    @Login
    @ResponseBody
    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        User user = userRepository.getById(Long.valueOf((String) request.getAttribute("userId")));
        request.setAttribute("username", user.getUsername());
        return "test";
    }

}
