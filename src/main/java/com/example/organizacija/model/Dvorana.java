package com.example.organizacija.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;


@Entity
@Table(name = "Dvorana", schema = "Sifrarnik")
@Getter
@Setter
public class Dvorana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DvoranaID")
    private Long dvoranaID;

    @Column(name = "Naziv")
    private String naziv;

    @Column(name = "Kapacitet")
    private Long kapacitet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KazalisteID")
    private Kazaliste kazaliste;
}

