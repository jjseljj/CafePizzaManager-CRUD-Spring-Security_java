package com.example.PizzaProject.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public static NoOpPasswordEncoder getEncoder()
    {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain getChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(
                        auth ->
                                auth

                                        .requestMatchers(toH2Console()).permitAll()
                                        .requestMatchers("/h2-console/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/index.html", "/", "/open", "/h2-console**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
                                        .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                                        .anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .httpBasic(Customizer.withDefaults())
                .logout() //     POST /logout
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        ;
        return http.build();
    }

    @Bean
    public MyUserDetailsService userDetailsService()
    {
        return new MyUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

}

