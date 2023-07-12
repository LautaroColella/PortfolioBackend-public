package com.lautarocolella.portfolio.security;

import com.lautarocolella.portfolio.model.User;
import com.lautarocolella.portfolio.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImp implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo
                .findOneByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email " + email + " was not found."));

        return new UserDetailsImpl(user);
    }
}
