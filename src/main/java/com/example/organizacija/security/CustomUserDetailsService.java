package com.example.organizacija.security;
import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.model.Organizator;
import com.example.organizacija.repository.PosjetiteljRepository;
import com.example.organizacija.repository.OrganizatorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PosjetiteljRepository posRepo;
    private final OrganizatorRepository orgRepo;

    public CustomUserDetailsService(PosjetiteljRepository p, OrganizatorRepository o){
        this.posRepo = p;
        this.orgRepo = o; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        email = email == null ? "" : email.trim().toLowerCase();

        // ADMIN preko organizatora
        Organizator org = orgRepo.findByEmail(email).orElse(null);
        if(org != null){
            return new User(
                    org.getEmail(), org.getPassword(),true,
                true,true,true,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        // USER preko posjetitelja
        Posjetitelj pos = posRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nije pronađen"));
        return new User(
                pos.getEmail(), pos.getPassword(), pos.isEnabled(),
            true,true,true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}