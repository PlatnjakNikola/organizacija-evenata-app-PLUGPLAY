package com.example.organizacija.controller;

import com.example.organizacija.model.Dogadaj;
import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.model.Recenzija;
import com.example.organizacija.repository.DogadajRepository;
import com.example.organizacija.repository.PosjetiteljRepository;
import com.example.organizacija.repository.RecenzijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/recenzije")
@RequiredArgsConstructor
public class RecenzijaController {

    private final RecenzijaRepository recRepo;
    private final DogadajRepository dogRepo;
    private final PosjetiteljRepository posRepo;

    @GetMapping
    public String dispatch(Authentication auth, Model m) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        m.addAttribute("active","recenzije");
        m.addAttribute("pageTitle","Recenzije");

        if (isAdmin) {
            // ADMIN: read-only tablica svih recenzija
            m.addAttribute("recenzije", recRepo.findAll());
            return "recenzije/list";
        } else {
            // USER: vidi SAMO svoje + forma
            String email = auth.getName();
            m.addAttribute("recenzije", recRepo.findByPosjetitelj_Email(email));
            m.addAttribute("recenzija", new Recenzija());
            m.addAttribute("dogadaji", dogRepo.findAll());
            m.addAttribute("ocjene", List.of(1,2,3,4,5));
            return "recenzije/form";
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Authentication auth, Model m) {
        Recenzija r = recRepo.findById(id).orElseThrow();
        enforceOwner(r, auth); // samo vlasnik smije uređivati

        m.addAttribute("active","recenzije");
        m.addAttribute("pageTitle","Recenzije");
        m.addAttribute("recenzija", r);
        m.addAttribute("recenzije", recRepo.findByPosjetitelj_Email(auth.getName()));
        m.addAttribute("dogadaji", dogRepo.findAll());
        m.addAttribute("ocjene", List.of(1,2,3,4,5));
        return "recenzije/form";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/save")
    public String save(@ModelAttribute Recenzija rec,
                       @RequestParam("dogadajId") Long dogadajId,
                       Authentication auth) {

        Posjetitelj me = posRepo.findByEmail(auth.getName()).orElseThrow();
        Dogadaj dog = dogRepo.findById(dogadajId).orElseThrow();

        if (rec.getRecenzijaID() == null) {
            // CREATE – prisilno postavi vlasnika i datum
            rec.setPosjetitelj(me);
            rec.setDogadaj(dog);
            if (rec.getDatumRecenzije() == null) rec.setDatumRecenzije(LocalDate.now());
            recRepo.save(rec);
        } else {
            // UPDATE – dovuci iz baze, provjeri vlasnika, pa ažuriraj dopuštena polja
            Recenzija db = recRepo.findById(rec.getRecenzijaID()).orElseThrow();
            enforceOwner(db, auth);
            db.setDogadaj(dog);
            db.setOcjena(rec.getOcjena());
            db.setKomentar(rec.getKomentar());
            recRepo.save(db);
        }
        return "redirect:/recenzije";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        Recenzija r = recRepo.findById(id).orElseThrow();
        enforceOwner(r, auth); // samo vlasnik smije brisati
        recRepo.delete(r);
        return "redirect:/recenzije";
    }

    private void enforceOwner(Recenzija r, Authentication auth) {
        String email = auth.getName();
        if (r.getPosjetitelj() == null || r.getPosjetitelj().getEmail() == null ||
                !r.getPosjetitelj().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
