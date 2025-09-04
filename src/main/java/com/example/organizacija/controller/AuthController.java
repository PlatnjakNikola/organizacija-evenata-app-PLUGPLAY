package com.example.organizacija.controller;
import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService s){
        this.service=s;
    }

    @GetMapping("/register")
    public String regForm(Model m){
        m.addAttribute("posjetitelj", new Posjetitelj());
        return "auth/register";
    }

    @PostMapping("/register")
    public String reg(@ModelAttribute("posjetitelj") Posjetitelj p){
        try {
            service.register(p);
            return "redirect:/auth/login?success";
        } catch (IllegalArgumentException ex) {
            // dupli email ili loš token-store ( vrati na formu s porukom)
            return "redirect:/auth/register?error=" + ex.getMessage();
        }
    }

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token){
        try {
            service.confirmToken(token);
            return "redirect:/auth/login?confirmed";
        } catch (IllegalArgumentException ex) {
            return "redirect:/auth/login?confirmError";
        }
    }
}