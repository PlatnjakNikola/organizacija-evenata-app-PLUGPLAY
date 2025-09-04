package com.example.organizacija.repository;
import com.example.organizacija.model.Dogadaj;
import com.example.organizacija.model.Recenzija;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RecenzijaRepository extends JpaRepository<Recenzija, Long> {

    List<Recenzija> findByDogadaj(Dogadaj dogadaj);
    List<Recenzija> findByDogadaj_DogadajID(Long dogadajID);


    List<Recenzija> findAllByPosjetitelj_Email(String email);

    List<Recenzija> findByPosjetitelj_Email(String email);
}