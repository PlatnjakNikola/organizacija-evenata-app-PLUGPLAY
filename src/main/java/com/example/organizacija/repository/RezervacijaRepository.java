package com.example.organizacija.repository;
import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.model.Rezervacija;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RezervacijaRepository extends JpaRepository<Rezervacija, Long> {
    List<Rezervacija> findByPosjetitelj_Email(String email);
    // ponekad zgodno:
    List<Rezervacija> findByDogadaj_DogadajID(Long dogadajID);
}
