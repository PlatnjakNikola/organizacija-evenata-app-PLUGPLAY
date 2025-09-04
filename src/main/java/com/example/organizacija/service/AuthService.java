package com.example.organizacija.service;

import com.example.organizacija.model.Posjetitelj;
import com.example.organizacija.repository.PosjetiteljRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final PosjetiteljRepository repo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder encoder;

    // dev in-memory token store; za produkciju prebaci u tablicu
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public AuthService(PosjetiteljRepository r, JavaMailSender m, PasswordEncoder e) {
        this.repo = r;
        this.mailSender = m;
        this.encoder = e;
    }

    @Transactional
    public void register(Posjetitelj p) {
        // normaliziraj email
        String email = p.getEmail().trim().toLowerCase();
        p.setEmail(email);

        // account disabled dok ne potvrdi
        p.setEnabled(false);

        // hash lozinke
        p.setPassword(encoder.encode(p.getPassword()));

        // save; unique indeks hvata utrke
        try {
            repo.save(p);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Korisnik s tim emailom vec postoji.", ex);
        }

        // generiraj i posalji token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, email);
        sendConfirmationMail(email, token);
    }

    @Transactional
    public void confirmToken(String token) {
        String email = tokenStore.remove(token); // jednokratno
        if (email == null) {
            throw new IllegalArgumentException("Nevazeci ili iskoristen token.");
        }
        Posjetitelj p = repo.findByEmail(email).orElseThrow();
        p.setEnabled(true);
        repo.save(p);
    }

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private void sendConfirmationMail(String email, String token) {
        String url = baseUrl + "/auth/confirm?token=" + token;
        System.out.println("CONFIRM URL: " + url); // pomoc u dev-u

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Potvrda registracije");
        msg.setText("Klikni za potvrdu: " + url);
        msg.setFrom("nikola.platnjak1@gmail.com"); // neka bude isti kao spring.mail.username

        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            System.err.println("Slanje maila nije uspjelo: " + ex.getMessage());
        }
    }
}
