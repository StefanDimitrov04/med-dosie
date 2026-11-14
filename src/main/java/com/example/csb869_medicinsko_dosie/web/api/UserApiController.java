package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.dto.RegisterDTO;
import com.example.csb869_medicinsko_dosie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/register")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
