package com.sparta.bulletin.controller;

import com.sparta.bulletin.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping("/success")
    public String loginSuccess(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        return "index";
    }
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            model.addAttribute("username", userDetails.getUsername());
        } catch(NullPointerException e){
            model.addAttribute("username", null);
        }
        return "index";
    }
}
