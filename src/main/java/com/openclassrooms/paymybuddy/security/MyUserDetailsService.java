package com.openclassrooms.paymybuddy.security;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    private  IUserRepository userRepository;

    @Autowired
    public MyUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = userRepository.findByEmail(username);
      if(user == null){
          throw  new UsernameNotFoundException("User not found:" + username);
      }
        log.info("myServiceDetailscontextauthentication:" + SecurityContextHolder.getContext().getAuthentication());
        return new MyUserDetails(user.getEmail(),user.getPassword());
    }

}
