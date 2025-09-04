package com.example.organizacija.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Rezervacija", schema = "IO")
@Setter
@Getter
public class Rezervacija {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RezervacijaID")
    private Long rezervacijaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PosjetiteljID", nullable = false)
    private Posjetitelj posjetitelj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DogadajID", nullable = false)
    private Dogadaj dogadaj;

    @Column(name = "DatumRezervacije")
    private LocalDate datumRezervacije;

    @Column(name = "BrojMjesta")
    private int brojMjesta;

    @Column(name = "UkupnaCijena")
    private BigDecimal ukupnaCijena;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 32)
    private RezervacijaStatus  status = RezervacijaStatus.NA_CEKANJU;

    public Rezervacija() { }
}