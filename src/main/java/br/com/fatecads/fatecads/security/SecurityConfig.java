package br.com.fatecads.fatecads.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(
                                                                "/esqueci-senha",
                                                                "/redefinir-senha"))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/login",
                                                                "/esqueci-senha",
                                                                "/redefinir-senha",
                                                                "/fatecads",
                                                                "/css/**",
                                                                "/images/**",
                                                                "/usuarios/criar",
                                                                "/usuarios/salvar")
                                                .permitAll()
                                                        .requestMatchers("/marketplace", "/produtos/imagem/**", "/carrinho/**", "/checkout/**")
                                                                .hasAnyRole("USER", "ADMIN")
                                                        .requestMatchers("/home", "/alunos/**", "/cursos/**", "/disciplinas/**",
                                                                "/professores/**", "/produtos/**", "/pedidos/**", "/usuarios/**")
                                                                .hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                        .successHandler((request, response, authentication) -> {
                                                boolean admin = authentication.getAuthorities().stream()
                                                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
                                                response.sendRedirect(admin ? "/home" : "/marketplace");
                                        })
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }

}