package com.example.LiveChat.Service;

import com.example.LiveChat.Model.Admin;
import com.example.LiveChat.Repository.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepo.findByEmail(email);
        if (admin.isEmpty()) {
            throw new UsernameNotFoundException("Admin not found");
        }

        return User.withUsername(admin.get().getEmail())
                .password(admin.get().getPassword())
                .roles("ADMIN")
                .build();
    }
}
