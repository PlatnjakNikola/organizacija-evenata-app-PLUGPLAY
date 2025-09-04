package com.example.organizacija.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Dogadaj", schema = "IO")
@Getter
@Setter
public class Dogadaj {

    @Setter
    @Getter
    @OneToMany(mappedBy = "dogadaj", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("poredak asc, dogadajSlikaID asc")
    private List<DogadajSlika> slike = new java.util.ArrayList<>();

    @Transient
    public String getCoverUrl() {
        if (slike == null) return null;
        return slike.stream()
                .filter(s -> s.getUloga() == UlogaSlike.COVER && s.getUrl() != null && !s.getUrl().isBlank())
                .map(DogadajSlika::getUrl)
                .findFirst()
                .orElse(null);
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DogadajID")
    private Long dogadajID;

    @Column(name = "Naziv")
    private String naziv;

    @Column(name = "Opis")
    private String opis;

    @Column(name = "DatumVrijeme")
    private LocalDateTime datumVrijeme;

    @Column(name = "CijenaUlaznice")
    private BigDecimal cijenaUlaznice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DvoranaID")
    private Dvorana dvorana;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrganizatorID")
    private Organizator organizator;

    @OneToMany(mappedBy = "dogadaj", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recenzija> recenzije = new ArrayList<>();

    @OneToMany(mappedBy = "dogadaj", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rezervacija> rezervacije = new ArrayList<>();


    // getters and setters


}