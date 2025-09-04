package com.example.organizacija.service;
import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.model.RezervacijaStatus;
import com.example.organizacija.repository.PosjetiteljRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RezervacijaService {
    @PersistenceContext private EntityManager em;
    private final PosjetiteljRepository posRepo;
    public RezervacijaService(PosjetiteljRepository p){
        this.posRepo=p;
    }
    @Transactional
    public void dodajRezervaciju(String email, Long dogadajID, int brojMjesta){
        Posjetitelj p = posRepo.findByEmail(email).orElseThrow();
        StoredProcedureQuery q = em.createStoredProcedureQuery("DodajRezervaciju");
        q.registerStoredProcedureParameter("BrojMjesta",Integer.class,ParameterMode.IN);
        q.registerStoredProcedureParameter("Status",String.class,ParameterMode.IN);
        q.registerStoredProcedureParameter("PosjetiteljID",Integer.class,ParameterMode.IN);
        q.registerStoredProcedureParameter("DogadajID",Integer.class,ParameterMode.IN);
        q.setParameter("BrojMjesta",brojMjesta);
        q.setParameter("Status", RezervacijaStatus.NA_CEKANJU.name());
        q.setParameter("PosjetiteljID",p.getPosjetiteljID().intValue());
        q.setParameter("DogadajID",dogadajID.intValue());
        q.execute();
    }
}