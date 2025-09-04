package com.example.organizacija.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Posjetitelj", schema = "Sifrarnik")
@Getter
@Setter
public class Posjetitelj {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long posjetiteljID;

    private String ime;
    private String prezime;

    @Column(length = 11)
    private String oib;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

}