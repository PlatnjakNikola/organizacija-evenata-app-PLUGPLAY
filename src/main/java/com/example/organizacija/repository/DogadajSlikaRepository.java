package com.example.organizacija.repository;

import com.example.organizacija.model.DogadajSlika;
import com.example.organizacija.model.UlogaSlike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogadajSlikaRepository extends JpaRepository<DogadajSlika, Long> {
    Optional<DogadajSlika> findFirstByDogadaj_DogadajIDAndUloga(Long dogadajID, UlogaSlike uloga);
}
