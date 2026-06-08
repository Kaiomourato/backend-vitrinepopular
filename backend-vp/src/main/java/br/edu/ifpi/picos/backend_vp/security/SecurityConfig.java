package br.edu.ifpi.picos.backend_vp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    // ── CORS ─────────────────────────────────────────────────────────────────
    // Configurar CORS aqui (no nível do Security) é obrigatório.
    // Configurar só nos @CrossOrigin dos controllers não funciona porque o
    // Spring Security intercepta o preflight OPTIONS antes de chegar neles.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Aceitar qualquer origem (altere para o domínio do seu frontend em produção)
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos HTTP permitidos — incluir OPTIONS é essencial para o preflight
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers que o frontend pode enviar
        config.setAllowedHeaders(List.of("*"));

        // Expor o header Authorization para que o frontend possa ler o token
        config.setExposedHeaders(List.of("Authorization"));

        // Necessário quando allowedOriginPatterns tem padrão específico e o
        // frontend envia cookies ou o header Authorization
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ── Security Filter Chain ────────────────────────────────────────────────
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Ativar CORS usando o bean acima antes de qualquer coisa
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF desativado — usamos JWT stateless, não sessões/cookies
                .csrf(csrf -> csrf.disable())

                // Sem sessão HTTP — cada request é autenticado pelo token
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(req -> {
                    // Preflight OPTIONS sempre liberado (necessário para CORS)
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Rotas públicas de autenticação
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll();

                    // Feed público — qualquer um pode navegar sem login
                    req.requestMatchers(HttpMethod.GET, "/api/ofertas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/lojas/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll();

                    // Swagger público para testes
                    req.requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**"
                    ).permitAll();

                    // Tudo mais exige token JWT válido
                    req.anyRequest().authenticated();
                })

                // Nosso filtro JWT roda antes do filtro padrão de usuário/senha
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ── Beans de suporte ─────────────────────────────────────────────────────

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}