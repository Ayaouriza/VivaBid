package com.example.demo;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
      return args -> {
        if (!userRepository.existsByUsername("responsableSI")) {
            userRepository.save(new User(null, "responsableSI", passwordEncoder.encode("password123"), Role.RESPONSABLE_SI));
            System.out.println("Utilisateur responsableSI créé !");
        }

        if (!userRepository.existsByUsername("backoffice")) {
            userRepository.save(new User(null, "backoffice", passwordEncoder.encode("password123"), Role.RESPONSABLE_BACK_OFFICE));
            System.out.println("Utilisateur backoffice créé !");
        }

        if (!userRepository.existsByUsername("agentParc")) {
            userRepository.save(new User(null, "agentParc", passwordEncoder.encode("password123"), Role.AGENT_PARC));
            System.out.println("Utilisateur agentParc créé !");
        }

        if (!userRepository.existsByUsername("agentSaisie")) {
            userRepository.save(new User(null, "agentSaisie", passwordEncoder.encode("password123"), Role.AGENT_SAISIE));
            System.out.println("Utilisateur agentSaisie créé !");
        }
     };
    }
}