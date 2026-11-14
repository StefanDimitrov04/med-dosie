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

    @NotBlank(message = "Потребителското име е задължително")
    @Size(min = 3, max = 50, message = "Минимум 3 символа")
    private String username;

    @NotBlank(message = "Паролата е задължителна")
    @Size(min = 4, message = "Минимум 4 символа")
    private String password;

    // "PATIENT" or "DOCTOR"
    private String role;

    // For PATIENT
    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 100)
    private String name;

    @Pattern(regexp = "\\d{10}", message = "ЕГН-то трябва да е точно 10 цифри")
    private String egn;

    private Long generalPractitionerId;

    // For DOCTOR
    private String identificationNumber;
    private String speciality;
    private Long existingDoctorId; // link to existing doctor profile
}
