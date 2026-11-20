package com.airline.service.Impl;

import com.airline.enums.UserRole;
import com.airline.model.User;
import com.airline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    private void initializeAdminUser(){
        String email="ram@gmail.com";
        String password="codewithram";

        if(userRepository.findByEmail(email)==null){
            User adminUser=User
                    .builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .fullName("ram")
                    .role(UserRole.ROLE_SYSTEM_ADMIN)
                    .build();

            User admin=userRepository.save(adminUser);
        }
    }
}
