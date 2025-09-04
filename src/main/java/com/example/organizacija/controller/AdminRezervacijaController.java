package com.example.organizacija.controller;
import com.example.organizacija.service.AdminRezervacijaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/rezervacije")
public class AdminRezervacijaController {
    private final AdminRezervacijaService service;
    public AdminRezervacijaController(AdminRezervacijaService s){this.service=s;}

    @GetMapping("/sve")
    @PreAuthorize("hasRole('ADMIN')")
    public String sve(Model m){
        m.addAttribute("rezervacije",service.findAll());
        return "rezervacije/admin-list";
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String status(@RequestParam Long rezervacijaID,@RequestParam String noviStatus){
        service.updateStatus(rezervacijaID,noviStatus);
        return "redirect:/rezervacije/sve";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id){
        service.deleteRezervacija(id);
        return "redirect:/rezervacije/sve";
    }
}