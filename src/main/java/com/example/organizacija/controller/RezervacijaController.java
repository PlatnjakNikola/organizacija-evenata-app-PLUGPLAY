package com.example.organizacija.controller;

import com.example.organizacija.model.Rezervacija;
import com.example.organizacija.model.RezervacijaStatus;
import com.example.organizacija.repository.DogadajRepository;
import com.example.organizacija.repository.PosjetiteljRepository;
import com.example.organizacija.repository.RezervacijaRepository;
import com.example.organizacija.service.RezervacijaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Controller
@RequestMapping("/rezervacije")
@RequiredArgsConstructor
public class RezervacijaController {

    private final RezervacijaRepository rezRepo;
    private final DogadajRepository dogRepo;
    private final PosjetiteljRepository posRepo;
    private final RezervacijaService rezService;

        @GetMapping
        public String dispatch(Authentication auth, Model m) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            m.addAttribute("active","rezervacije");
            m.addAttribute("pageTitle","Rezervacije");


            if (isAdmin) {
                m.addAttribute("rezervacije", rezRepo.findAll()); // za tablicu (oboje vide)
                Rezervacija r = new Rezervacija(); // default status već imaš na polju
                m.addAttribute("rezervacija", r);
                m.addAttribute("dogadaji", dogRepo.findAll());
                m.addAttribute("posjetitelji", posRepo.findAll());
                m.addAttribute("statusi", RezervacijaStatus.values());
                return "rezervacije/form";
            } else {
                String email = auth.getName();
                m.addAttribute("rezervacije", rezRepo.findByPosjetitelj_Email(email)); // za tablicu (oboje vide)
                return "rezervacije/list";
            }
        }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model m){
        m.addAttribute("active","rezervacije");
        m.addAttribute("pageTitle","Rezervacije");
        m.addAttribute("rezervacija", rezRepo.findById(id).orElseThrow());
        m.addAttribute("rezervacije", rezRepo.findAll());
        m.addAttribute("dogadaji", dogRepo.findAll());
        m.addAttribute("posjetitelji", posRepo.findAll());
        m.addAttribute("statusi", RezervacijaStatus.values());
        return "rezervacije/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute Rezervacija rezervacija,
                       @RequestParam("dogadajId") Long dogadajId,
                       @RequestParam("posjetiteljId") Long posjetiteljId) {

        var dog = dogRepo.findById(dogadajId).orElseThrow();
        var pos = posRepo.findById(posjetiteljId).orElseThrow();

        rezervacija.setDogadaj(dog);
        rezervacija.setPosjetitelj(pos);

        // serverska polja – NE stavljati u formu
        if (rezervacija.getDatumRezervacije() == null) {
            rezervacija.setDatumRezervacije(java.time.LocalDate.now());
        }
        if (rezervacija.getBrojMjesta() == 0) rezervacija.setBrojMjesta(1);

        // ukupna = cijena * brojMjesta (pretpostavka: cijenaUlaznice je BigDecimal)
        if (dog.getCijenaUlaznice() != null) {
            java.math.BigDecimal total = dog.getCijenaUlaznice()
                    .multiply(java.math.BigDecimal.valueOf(rezervacija.getBrojMjesta()));
            rezervacija.setUkupnaCijena(total);
        }

        rezRepo.save(rezervacija); // radi i INSERT i UPDATE (ako ima rezervacijaID)
        return "redirect:/rezervacije";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id){
        rezRepo.deleteById(id);
        return "redirect:/rezervacije";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String create(Authentication auth,
                         @RequestParam("dogadajId") Long dogadajId,
                         @RequestParam("brojMjesta") int brojMjesta) {

        String email = auth.getName();
        rezService.dodajRezervaciju(email, dogadajId, brojMjesta);

        return "redirect:/rezervacije";
    }
}
