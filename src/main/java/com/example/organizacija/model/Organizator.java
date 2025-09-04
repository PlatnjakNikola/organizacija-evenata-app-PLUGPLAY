package com.example.organizacija.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Organizator", schema = "Sifrarnik")
@Getter
@Setter
public class Organizator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizatorID;
    private String naziv;

    @Column(length = 11)
    private String oib;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}