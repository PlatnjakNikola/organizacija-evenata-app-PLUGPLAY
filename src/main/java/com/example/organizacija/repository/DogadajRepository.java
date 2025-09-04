package com.example.organizacija.repository;
import com.example.organizacija.model.Dogadaj;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogadajRepository extends JpaRepository<Dogadaj, Long> {
    // da ne radimo N+1 kad prikazujemo tablicu:
    @EntityGraph(attributePaths = {"dvorana","organizator"})
    List<Dogadaj> findAll();
}