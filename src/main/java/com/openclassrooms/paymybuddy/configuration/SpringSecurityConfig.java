package com.openclassrooms.paymybuddy.configuration;

import com.openclassrooms.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
            auth.userDetailsService(userDetailsService);

//      auth.inMemoryAuthentication()
//         .withUser("spring@email.fr")
//              .password(passwordEncoder().encode("admin123"))
//              .roles("USER");
}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
////                .csrf().disable()
//                .authorizeRequests()
////                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers("/").hasRole("USER")
//                .antMatchers("/index").hasRole("USER")
//                .antMatchers("/addfriend").hasRole("USER")
//                .antMatchers("/customlogin").hasRole("USER")
//                .antMatchers("/css/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
////                .httpBasic()
//                .formLogin()
////                .usernameParameter("username")
////                .usernameParameter("password")
//                .loginPage("/login")
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl( "/index", true )
//                .failureUrl("/login?error")
//                .permitAll()
//                ;

//                .and()
//                .oauth2Login();
        http.csrf().disable();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userService);
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
