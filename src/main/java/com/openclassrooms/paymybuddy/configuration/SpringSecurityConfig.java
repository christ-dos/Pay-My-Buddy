package com.openclassrooms.paymybuddy.configuration;

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

import javax.sql.DataSource;

/**
 * Class of configuration SpringSecurity
 *
 * @author Christine Duarte
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

//    @Autowired
//    private DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
//        auth.userDetailsService(userDetailsService);

//        auth.inMemoryAuthentication()
//                .withUser("spring@email.fr")
//                .password(passwordEncoder().encode("admin123"))
//                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//
        http
                .authorizeRequests()
                .antMatchers("/home")
//                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers("/user").hasRole("USER")
                .authenticated()
                .anyRequest().permitAll()
              .and()
                .formLogin()
                .usernameParameter("email")
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
                .logout().
                logoutSuccessUrl("/login").permitAll();
    }

}

//

//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
////                .csrf().disable()
//                .authorizeRequests()
////                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers("/home").hasRole("USER")
//                .antMatchers("/transaction").hasRole("USER")
//                .antMatchers("/transfer").hasRole("USER")
//                  .antMatchers("/profile").hasRole("USER")
//                .antMatchers("/contact").hasRole("USER")
//                .antMatchers("/addfriend").hasRole("USER")
//                .antMatchers("/login").hasRole("USER")
//                .antMatchers("/css/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
////                .usernameParameter("username")
////                .usernameParameter("password")
//                .loginPage("/login").permitAll()
////                .loginProcessingUrl("/login")
//                .defaultSuccessUrl( "/home", true )
//                .failureUrl("/login?error")
//                .and()
//                .httpBasic()
//                ;
//
////                .and()
////                .oauth2Login();
////
//    }
//

//
//    @Bean
//    public PasswordEncoder getPasswordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//}
