package com.bridge.medic.config.security;

import com.bridge.medic.config.security.auditing.ApplicationAuditAware;
import com.bridge.medic.config.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.bridge.medic.config.security.authorization.PermissionEnum.*;
import static com.bridge.medic.config.security.authorization.RoleEnum.ADMIN;
import static com.bridge.medic.config.security.authorization.RoleEnum.SUPPORT;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${application.client.url}")
    private String clientUrl;
    @Value("${application.server.url}")
    private String serverUrl;

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security"
    };

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**"
    };
    private static final String[] TEST_WHITELIST = {
            "/api/v1/**"
    };
    private static final String[] WHITELIST = {
            "/api/v1/users/specialist-search",
            "/api/v1/appointments/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                                req.requestMatchers(SWAGGER_WHITELIST).permitAll()
                                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                                .requestMatchers(TEST_WHITELIST).permitAll()
                                        .requestMatchers(WHITELIST).permitAll()
                                        .requestMatchers("/api/v1/appointments/**").authenticated()
                                        .requestMatchers(POST, "/api/v1/appointments/book/**").authenticated()


                                        .requestMatchers("/api/v1/doctor/**").hasAnyRole(ADMIN.name(), SUPPORT.name())
                                        .requestMatchers(GET, "/api/v1/doctor/**").hasAnyAuthority(ADMIN_READ.name(), SUPPORT.name())
                                        .requestMatchers(POST, "/api/v1/doctor/**").hasAnyAuthority(ADMIN_CREATE.name(), SUPPORT.name())
                                        .requestMatchers(PUT, "/api/v1/doctor/**").hasAnyAuthority(ADMIN_UPDATE.name(), SUPPORT.name())
                                        .requestMatchers(DELETE, "/api/v1/doctor/**").hasAnyAuthority(ADMIN_DELETE.name(), SUPPORT.name())

                                        .anyRequest()
                                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
                .cors(withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(clientUrl, serverUrl, clientUrl + "/user"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuditorAware<Integer> auditorAware() {
        return new ApplicationAuditAware();
    }
}
