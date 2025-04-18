package com.travel_payment.cnpm.configurations;

import com.travel_payment.cnpm.cors.WebConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String[] PUBLIC_MATCHERS = {
            "/api/v1/auth/login",
            "/api/v1/auth/signup",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/verify-account",
            "/api/v1/auth/regenerate-otp",
            "/api/v1/auth/test",
            "/api/v1/auth/set-password/**",
            "/api/v1/auth/verify-forgot-password",
            "/api/v1/tour/all-tours",
            "/api/v1/tour/all-tour/category/**",
            "/api/v1/tour/tour-details/**",
            "/api/v1/category/all/**",
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(WebConfig.corsConfigurationSource())
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(PUBLIC_MATCHERS).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    }
}
