package ru.develop.bank.jwt;


import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
     private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           return http
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/admin/user").permitAll()

                            .anyRequest().authenticated()
                            )

                   .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                    .build();


//        return http
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeHttpRequests(
//                        authz -> authz
//                                .antMatchers("/api/auth/login", "/api/auth/token").permitAll()
//                                .anyRequest().authenticated()
//                                .and()
//                                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                ).build();


    }

    //@Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //    http.authorizeRequests()
    //      .anyRequest().authenticated()
    //      .and().httpBasic();
    //    return http.build();
    //}
}

