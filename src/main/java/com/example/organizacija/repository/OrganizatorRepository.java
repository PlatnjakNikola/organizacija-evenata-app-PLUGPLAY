package com.example.organizacija.repository;
import com.example.organizacija.model.Organizator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrganizatorRepository extends JpaRepository<Organizator, Long> {
    Optional<Organizator> findByEmail(String email);
}