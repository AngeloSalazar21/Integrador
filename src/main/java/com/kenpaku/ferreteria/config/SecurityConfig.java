package com.kenpaku.ferreteria.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginAttemptService loginAttemptService;
    private final LoginAttemptFilter loginAttemptFilter;

    public SecurityConfig(LoginAttemptService loginAttemptService, LoginAttemptFilter loginAttemptFilter) {
        this.loginAttemptService = loginAttemptService;
        this.loginAttemptFilter = loginAttemptFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/login").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        http.addFilterBefore(loginAttemptFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler loginFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                    jakarta.servlet.http.HttpServletResponse response,
                    org.springframework.security.core.AuthenticationException exception)
                    throws java.io.IOException, jakarta.servlet.ServletException {
                int attempts = loginAttemptService.incrementFailedAttempt(request);
                if (attempts >= LoginAttemptService.MAX_ATTEMPTS) {
                    setDefaultFailureUrl("/login?locked");
                } else {
                    setDefaultFailureUrl("/login?error");
                }
                super.onAuthenticationFailure(request, response, exception);
            }
        };
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                    jakarta.servlet.http.HttpServletResponse response,
                    org.springframework.security.core.Authentication authentication)
                    throws java.io.IOException, jakarta.servlet.ServletException {
                loginAttemptService.reset(request);
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
