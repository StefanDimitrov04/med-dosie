package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
