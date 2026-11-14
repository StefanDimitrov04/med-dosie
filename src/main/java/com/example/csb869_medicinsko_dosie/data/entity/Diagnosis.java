package com.example.csb869_medicinsko_dosie.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis extends BaseEntity {

    @NotBlank(message = "Diagnosis name is required")
    @Size(min = 2, max = 200)
    @Column(nullable = false)
    private String name;

    @Size(max = 500)
    private String description;
}
