package com.springauthorization.demo.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/userInfo")
    @PreAuthorize("hasRole('User', 'Admin')")
    public String[] getUserInfo() { return new String[] {"user","userInfo"}; }
}
