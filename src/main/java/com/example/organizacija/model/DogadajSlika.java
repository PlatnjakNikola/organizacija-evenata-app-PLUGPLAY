package com.example.organizacija.model;

import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Dogadaj_Slika", schema = "IO")
@Getter
@Setter
public class DogadajSlika {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Dogadaj_SlikaID")
    private Long dogadajSlikaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DogadajID", nullable = false)
    private Dogadaj dogadaj;

    @Column(name = "Url", nullable = false, length = 512)
    private String url;

    @Column(name = "Alt")
    private String alt;

    @Enumerated(EnumType.STRING)
    @Column(name = "Uloga", nullable = false, length = 20)
    private UlogaSlike uloga = UlogaSlike.COVER;

    @Column(name = "Poredak")
    private Integer poredak;
}
