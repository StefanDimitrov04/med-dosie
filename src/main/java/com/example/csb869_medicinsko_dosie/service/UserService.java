package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.PublicRegisterDTO;
import com.example.csb869_medicinsko_dosie.dto.RegisterDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void register(RegisterDTO registerDTO);
    void registerPublic(PublicRegisterDTO dto);
    boolean usernameExists(String username);
}
