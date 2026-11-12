package co.edu.udes.ghostkitchen.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Reglas de acceso. Explorar es publico; cazar, resenar y unirse a retos exige
 * cuenta de cazador; el catalogo solo lo administra el rol ADMIN. La proteccion
 * CSRF queda activa para todos los formularios.
 */
@Configuration
public class ConfiguracionSeguridad {

    static final String POLITICA_DE_CONTENIDO = String.join("; ",
            "default-src 'self'",
            "img-src 'self' data: https://tile.openstreetmap.org",
            "style-src 'self' 'unsafe-inline'",
            "script-src 'self'",
            "font-src 'self'",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'self'",
            "form-action 'self'",
            "frame-ancestors 'none'");

    @Bean
    public PasswordEncoder cifradorDeClaves() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(acceso -> acceso
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/fuentes/**", "/webjars/**",
                                "/favicon.svg", "/error", "/error/**").permitAll()
                        .requestMatchers("/ingresar", "/registro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/explorar", "/cocinas/*", "/retos", "/ranking",
                                "/api/v1/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/perfil", "/acciones/**", "/cocinas/*/cazar", "/cocinas/*/resenas",
                                "/retos/*/unirse").hasRole("CAZADOR")
                        .anyRequest().authenticated())
                .formLogin(ingreso -> ingreso
                        .loginPage("/ingresar")
                        .loginProcessingUrl("/ingresar")
                        .usernameParameter("correo")
                        .passwordParameter("clave")
                        .successHandler(new RedireccionPorRol())
                        .failureUrl("/ingresar?error")
                        .permitAll())
                .logout(salida -> salida
                        .logoutUrl("/salir")
                        .logoutSuccessUrl("/?salida"))
                .headers(cabeceras -> cabeceras
                        .contentSecurityPolicy(politica -> politica.policyDirectives(POLITICA_DE_CONTENIDO))
                        .referrerPolicy(referencia -> referencia
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)));
        return http.build();
    }
}
