package com.example.organizacija.repository;
import com.example.organizacija.model.Posjetitelj;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PosjetiteljRepository extends JpaRepository<Posjetitelj, Long> {
    Optional<Posjetitelj> findByEmail(String email);
    boolean existsByEmail(String email);
}