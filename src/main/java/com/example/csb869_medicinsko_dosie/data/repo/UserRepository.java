package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
