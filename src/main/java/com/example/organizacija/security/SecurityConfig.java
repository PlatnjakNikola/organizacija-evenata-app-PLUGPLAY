package com.example.organizacija.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers("/auth/**", "/login", "/register",
                                "/css/**","/js/**","/images/**","/webjars/**",
                                "/error", "/error/**").permitAll()

                        // DOGAĐAJI
                        .requestMatchers(HttpMethod.GET,  "/dogadaji").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/dogadaji/edit/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/dogadaji/save").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/dogadaji/*/delete").hasRole("ADMIN")

                        // RECENZIJE
                        .requestMatchers(HttpMethod.GET, "/recenzije").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,  "/recenzije/edit/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/recenzije/save").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/recenzije/*/delete").hasRole("USER")

                        // REZERVACIJE
                        .requestMatchers(HttpMethod.GET,  "/rezervacije").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,  "/rezervacije/edit/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/rezervacije/save").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/rezervacije/*/delete").hasRole("ADMIN")

                        // VRIJEME (oboje)
                        .requestMatchers("/vrijeme/**").hasAnyRole("USER","ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login").permitAll()
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(roleBasedSuccessHandler())
                        .failureUrl("/auth/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )
                .authenticationProvider(daoAuthProvider());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (req, res, auth) -> res.sendRedirect("/dogadaji");
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(encoder());
        return p;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
