package com.movieticket.config;

import com.movieticket.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )

            .authorizeHttpRequests(auth -> auth

                // ================= PUBLIC =================
                .requestMatchers("/api/auth/**").permitAll()

                // ================= PUBLIC BROWSING =================
                .requestMatchers(HttpMethod.GET,
                        "/api/movies/**",
                        "/api/theaters/**",
                        "/api/shows/**"
                ).permitAll()

                // ================= SWAGGER =================
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-ui/index.html",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                // ================= USER MODULE =================
                .requestMatchers("/api/users/profile/**").hasAnyRole("USER","ADMIN")

             // ================= SEAT BOOKING MODULE =================
                .requestMatchers(HttpMethod.GET, "/api/seats/**").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/seats/**").hasRole("USER")

                // ================= BOOKING MODULE =================
                .requestMatchers("/api/bookings/my/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/bookings").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/bookings/{id}").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/cancel").hasAnyRole("USER","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/bookings/{id}/confirm").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/bookings").hasRole("ADMIN")

                // ================= PAYMENT MODULE =================
                .requestMatchers("/api/payments/initiate").hasRole("USER")
                .requestMatchers("/api/payments/verify").hasRole("USER")
                .requestMatchers("/api/payments/booking/**").hasAnyRole("USER","ADMIN")

                // ================= ADMIN MODULE =================
                .requestMatchers(HttpMethod.POST,
                        "/api/movies/**",
                        "/api/theaters/**",
                        "/api/shows/**"
                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT,
                        "/api/movies/**",
                        "/api/theaters/**",
                        "/api/shows/**"
                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                        "/api/movies/**",
                        "/api/theaters/**",
                        "/api/shows/**"
                ).hasRole("ADMIN")

                .requestMatchers("/api/users/all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                // ================= ANY OTHER =================
                .anyRequest().authenticated()
            )

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .userDetailsService(userDetailsService)

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + ex.getMessage() + "\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"" + ex.getMessage() + "\"}");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

   
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://movieticketsbookingfrontend.netlify.app/" 
        ));

       
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

       
        config.setAllowedHeaders(List.of("*"));

       
        config.setExposedHeaders(List.of("Authorization"));

        
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
