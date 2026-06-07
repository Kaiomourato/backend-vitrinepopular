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

@io.swagger.v3.oas.annotations.OpenAPIDefinition(security = {@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-key")})
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Desativa proteção contra ataques de sessão web (vamos usar tokens, não sessões)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API sem estado (Rest)
                .authorizeHttpRequests(req -> {
                    
                    // ROTAS TOTALMENTE PÚBLICAS (Qualquer um pode aceder)
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll();
                    
                    // O Feed de ofertas e listar lojas é público para quem apenas navega na app
                    req.requestMatchers(HttpMethod.GET, "/api/ofertas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/lojas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll();
                    
                    // O Swagger tem de ser público para você conseguir testar!
                    req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll();
                    
                    // TODAS AS OUTRAS ROTAS EXIGEM O TOKEN JWT (ex: Criar Oferta, Criar Loja)
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