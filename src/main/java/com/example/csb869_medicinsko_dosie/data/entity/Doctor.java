package com.example.csb869_medicinsko_dosie.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @NotBlank(message = "Doctor identifier number is required")
    @Column(unique = true, nullable = false)
    private String identificationNumber;

    @NotBlank(message = "Doctor name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Speciality is required")
    @Column(nullable = false)
    private String speciality;

    private boolean isGeneralPractitioner;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Examination> examinations;

    @OneToMany(mappedBy = "generalPractitioner")
    @JsonIgnore
    private Set<Patient> patients;
}
