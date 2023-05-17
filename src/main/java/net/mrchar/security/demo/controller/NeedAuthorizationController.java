package net.mrchar.security.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NeedAuthorizationController {
    @GetMapping("/needAuthenticated")
    public String needAuthorization() {
        return "ok";
    }
}
