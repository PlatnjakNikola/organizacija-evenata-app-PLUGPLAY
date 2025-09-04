package com.example.organizacija.service;
import com.example.organizacija.model.Rezervacija;
import com.example.organizacija.repository.RezervacijaRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminRezervacijaService {
    @PersistenceContext private EntityManager em;
    private final RezervacijaRepository repo;
    public AdminRezervacijaService(RezervacijaRepository r){this.repo=r;}
    public List<Rezervacija> findAll(){ return repo.findAll(); }
    @Transactional
    public void updateStatus(Long id,String noviStatus){
        StoredProcedureQuery q=em.createStoredProcedureQuery("AzurirajStatusRezervacije");
        q.registerStoredProcedureParameter("RezervacijaID",Integer.class,ParameterMode.IN);
        q.registerStoredProcedureParameter("NoviStatus",String.class,ParameterMode.IN);
        q.setParameter("RezervacijaID",id.intValue());
        q.setParameter("NoviStatus",noviStatus);
        q.execute();
    }
    @Transactional
    public void deleteRezervacija(Long id){repo.deleteById(id);}
}