package com.example.organizacija.controller;

import com.example.organizacija.model.Dogadaj;
import com.example.organizacija.model.DogadajSlika;
import com.example.organizacija.model.UlogaSlike;
import com.example.organizacija.repository.DogadajRepository;
import com.example.organizacija.repository.DvoranaRepository;
import com.example.organizacija.repository.OrganizatorRepository;
import com.example.organizacija.repository.DogadajSlikaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dogadaji")
@RequiredArgsConstructor
public class DogadajController {

    private final DogadajRepository dogRepo;
    private final DvoranaRepository dvorRepo;
    private final OrganizatorRepository orgRepo;
    private final DogadajSlikaRepository slikaRepo;

    @GetMapping
    public String dispatch(Authentication auth, Model m) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        m.addAttribute("active", "dogadaji");
        m.addAttribute("pageTitle", "Događaji");

        if (isAdmin) {
            m.addAttribute("dogadaj", new Dogadaj());
            m.addAttribute("dogadaji", dogRepo.findAll());
            m.addAttribute("dvorane", dvorRepo.findAll());
            m.addAttribute("organizatori", orgRepo.findAll());
            return "dogadaji/form";
        } else {
            m.addAttribute("dogadaji", dogRepo.findAll());
            return "dogadaji/list";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("active", "dogadaji");
        m.addAttribute("pageTitle", "Događaji");
        m.addAttribute("dogadaj", dogRepo.findById(id).orElseThrow());
        m.addAttribute("dogadaji", dogRepo.findAll());
        m.addAttribute("dvorane", dvorRepo.findAll());
        m.addAttribute("organizatori", orgRepo.findAll());
        return "dogadaji/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String save(@ModelAttribute Dogadaj form,
                       @RequestParam("dvoranaID") Long dvoranaID,
                       @RequestParam("organizatorID") Long organizatorID,
                       @RequestParam(value = "coverUrl", required = false) String coverUrl,
                       @RequestParam(value = "coverAlt", required = false) String coverAlt) {

        Dogadaj dogadaj;
        if (form.getDogadajID() != null) {
            // update: dohvati postojeći zapis
            dogadaj = dogRepo.findById(form.getDogadajID()).orElseThrow();

            dogadaj.setNaziv(form.getNaziv());
            dogadaj.setOpis(form.getOpis());
            dogadaj.setCijenaUlaznice(form.getCijenaUlaznice());
            dogadaj.setDatumVrijeme(form.getDatumVrijeme());
        } else {
            // create: novi dogadaj
            dogadaj = form;
        }

        dogadaj.setDvorana(dvorRepo.findById(dvoranaID).orElseThrow());
        dogadaj.setOrganizator(orgRepo.findById(organizatorID).orElseThrow());

        Dogadaj saved = dogRepo.save(dogadaj);

        // COVER handling
        if (coverUrl != null && !coverUrl.isBlank()) {
            var cover = slikaRepo
                    .findFirstByDogadaj_DogadajIDAndUloga(saved.getDogadajID(), UlogaSlike.COVER)
                    .orElseGet(() -> {
                        DogadajSlika s = new DogadajSlika();
                        s.setDogadaj(saved);
                        s.setUloga(UlogaSlike.COVER);
                        s.setPoredak(0);
                        return s;
                    });
            cover.setUrl(coverUrl.trim());
            cover.setAlt(coverAlt);
            slikaRepo.save(cover);
        } else {
            slikaRepo.findFirstByDogadaj_DogadajIDAndUloga(saved.getDogadajID(), UlogaSlike.COVER)
                    .ifPresent(slikaRepo::delete);
        }

        return "redirect:/dogadaji";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        dogRepo.deleteById(id);
        return "redirect:/dogadaji";
    }
}
