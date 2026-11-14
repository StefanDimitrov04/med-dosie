package com.example.csb869_medicinsko_dosie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDTO {
    private Long id;

    @NotBlank(message = "Diagnosis name is required")
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 500)
    private String description;
}
