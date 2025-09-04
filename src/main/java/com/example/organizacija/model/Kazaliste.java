package com.example.organizacija.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity

@Table(name = "Kazaliste", schema = "Sifrarnik")
@Setter
@Getter
public class Kazaliste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KazalisteID")
    private Long kazalisteID;

    @Column(name = "Naziv")
    private String naziv;

    @Column(name = "Adresa")
    private String adresa;

    @Column(name = "Grad")
    private String grad;
}
