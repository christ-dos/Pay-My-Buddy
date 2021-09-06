package com.openclassrooms.paymybuddy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Class of configuration SpringSecurity
 *
 * @author Christine Duarte
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Core interface which loads user-specific data
     * {@link UserDetailsService}
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * A bean that permit crypt the password before recording in the database
     *
     * @return A String containing the password encoded
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Implementation that retrieves user details from a
     * {@link UserDetailsService}.
     * to authenticate the User
     *
     * @return authentication set with the user find in database
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Method configure to determine the authentication provider
     * here provide from the database
     *
     * @param auth An {@link AuthenticationManagerBuilder} that create the authentication
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * Method that configure the security chain for requests HTTP
     *
     * @param http A incoming request
     * @throws Exception
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/login/**", "/signup").permitAll()
                .anyRequest().authenticated()
                .and()
                .rememberMe().userDetailsService(this.userDetailsService)
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("dummyCookie")
                .invalidateHttpSession(true);
    }
}
