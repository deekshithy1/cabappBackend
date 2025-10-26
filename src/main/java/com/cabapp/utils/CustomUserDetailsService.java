package com.cabapp.utils;

import com.cabapp.model.Captain;
import com.cabapp.model.User;
import com.cabapp.repository.ICaptainRepo;
import com.cabapp.repository.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private ICaptainRepo iCaptainRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user= userRepo.findByEmail(email).orElse(null);
        if(user!=null){
            return new CustomUser(user);
        }
        Captain captain= iCaptainRepo.findByEmail(email).orElse(null);
        if(captain!=null){
            return new CustomUser(captain);
        }
        throw new UsernameNotFoundException("User or Captain not found with email: " + email);
    }
}
