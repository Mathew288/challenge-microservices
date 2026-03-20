package com.ntt.ms_clientes.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	/**
	 * Mantiene Spring Security en el classpath, pero desactiva los mecanismos de
	 * autenticación por defecto (formLogin / httpBasic) y deja todas las rutas
	 * públicas.
	 *
	 * Esto evita que Spring Boot muestre el /login generado automáticamente.
	 */
	@Bean
	@ConditionalOnWebApplication(type = Type.SERVLET)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.formLogin(Customizer.withDefaults())
				.httpBasic(Customizer.withDefaults());

		// Desactivar los entrypoints por defecto para evitar redirecciones a /login
		http.formLogin(form -> form.disable());
		http.httpBasic(basic -> basic.disable());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
