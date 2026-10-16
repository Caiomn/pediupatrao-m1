package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Role;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                String adminUsername = System.getenv().getOrDefault("PEDIUPATRAO_ADMIN_USERNAME", "admin");
                var admin = repo.findByUsername(adminUsername);
                if (admin.isEmpty()) {
                    String initialPassword = System.getenv("PEDIUPATRAO_ADMIN_PASSWORD");
                    if (initialPassword == null || initialPassword.isBlank()) {
                        log.warn("No initial administrator created. Set PEDIUPATRAO_ADMIN_PASSWORD to enable bootstrap.");
                    } else {
                        Usuario user = new Usuario();
                        user.setUsername(adminUsername);
                        user.setPassword(passwordEncoder.encode(initialPassword));
                        user.setRole(Role.ADMIN);
                        repo.save(user);
                        log.info("Initial administrator '{}' created.", adminUsername);
                    }
                } else if (admin.get().getRole() == null) {
                    Usuario user = admin.get();
                    user.setRole(Role.ADMIN);
                    repo.save(user);
                }
            } catch (DataAccessException exception) {
                log.warn("MongoDB unavailable. The web application will stay online, but login and data require a database connection: {}",
                        exception.getMostSpecificCause().getMessage());
            }
        };
    }
}
