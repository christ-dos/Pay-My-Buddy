package com.openclassrooms.paymybuddy.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Class of configuration SpringSecurity
 *
 * @author Christine Duarte
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring()
//                .antMatchers("resources/**", "/static/**");
//    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
//
        http

                .authorizeRequests()
                .antMatchers("/css/**", "/login/**", "/signup").permitAll()
//                .antMatchers("/home").authenticated()
//                .antMatchers("/addfriend").authenticated()
//                .antMatchers("/transaction").authenticated()
                .antMatchers("/transfer").authenticated()
//                .antMatchers("/profie").authenticated()
//                .antMatchers("/contact").authenticated()
//                .antMatchers("/logoff").authenticated()
//                .and()
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
                .clearAuthentication(true)
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

    }


//    @Component
//    public class CustomAuthenticationProvider
//            implements AuthenticationProvider {
//
//        @Autowired
//        private UserDetailsService userDetailsService;
//
//        @Autowired
//        private PasswordEncoder passwordEncoder;
//
//        @Override
//        public Authentication authenticate(Authentication authentication) {
//            String username = authentication.getName();
//            String password = authentication.getCredentials().toString();
//
//            UserDetails u = userDetailsService.loadUserByUsername(username);
//
//            if (passwordEncoder.matches(password, u.getPassword())) {
//                return new UsernamePasswordAuthenticationToken(
//                        username,
//                        password,
//                        u.getAuthorities());
//            } else {
//                throw new BadCredentialsException
//                        ("Something went wrong!");
//            }
//        }
//
//        // Omitted code
//    }

//    public static  String getCurrentUser() {
//        SecurityContext auth = SecurityContextHolder.getContext();
////        auth.getAuthentication().getAuthorities();
//        MyUserDetails userAuthenticated = (MyUserDetails) auth.getAuthentication().getDetails();
//
//        return userAuthenticated.getUsername();
//    }

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
