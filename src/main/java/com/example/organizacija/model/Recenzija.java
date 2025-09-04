package com.example.organizacija.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Recenzija", schema = "IO")
@Getter
@Setter
public class Recenzija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecenzijaID")
    private Long recenzijaID;

    @Column(name = "Ocjena")
    private int ocjena;

    @Column(name = "Komentar")
    private String komentar;

    @Column(name = "DatumRecenzije")
    private LocalDate datumRecenzije;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PosjetiteljID", nullable = false)
    private Posjetitelj posjetitelj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dogadajID", nullable = false)
    private Dogadaj dogadaj;
}
