package br.edu.ifpi.picos.backend_vp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "bearer-key",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@io.swagger.v3.oas.annotations.OpenAPIDefinition(
        security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-key")}
)
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {

                    // ── SWAGGER — todos os paths que o SpringDoc registra ─────
                    // Spring Boot 4 + SpringDoc 2.x precisam de cada um explícito
                    req.requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll();

                    // ── ROTAS PÚBLICAS DA API ─────────────────────────────────
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll();

                    req.requestMatchers(HttpMethod.GET, "/api/ofertas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/lojas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll();

                    // ── TUDO O MAIS EXIGE TOKEN JWT ───────────────────────────
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}