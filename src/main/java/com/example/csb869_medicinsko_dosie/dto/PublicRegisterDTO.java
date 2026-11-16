package com.example.csb869_medicinsko_dosie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicRegisterDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Minimum 3 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Minimum 4 characters")
    private String password;

    private String role;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String name;

    @Pattern(regexp = "^(\\d{10})?$", message = "Personal ID must be exactly 10 digits")
    private String egn;

    private Long generalPractitionerId;

    private String identificationNumber;
    private String speciality;
    private Long existingDoctorId;
}
