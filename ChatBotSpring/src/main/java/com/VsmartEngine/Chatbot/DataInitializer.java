package com.VsmartEngine.Chatbot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.VsmartEngine.Chatbot.Admin.Role;
import com.VsmartEngine.Chatbot.Admin.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository rolerepository;
    
    @Override
    public void run(String... args) {
        insertRoleIfNotExists("ADMIN");
        insertRoleIfNotExists("AGENT");
    }

    private void insertRoleIfNotExists(String roleName) {
        rolerepository.findByRole(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setRole(roleName);
            return rolerepository.save(role);
        });
    }

}
