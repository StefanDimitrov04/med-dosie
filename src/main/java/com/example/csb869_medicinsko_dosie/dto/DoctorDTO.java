package com.example.csb869_medicinsko_dosie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {
    private Long id;

    @NotBlank(message = "Identification number is required")
    private String identificationNumber;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Speciality is required")
    private String speciality;

    private boolean isGeneralPractitioner;
}
