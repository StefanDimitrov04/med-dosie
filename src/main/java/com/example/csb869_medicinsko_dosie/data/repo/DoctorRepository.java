package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByIdentificationNumber(String identificationNumber);
    List<Doctor> findByIsGeneralPractitionerTrue();

    @Query("SELECT d, COUNT(p) FROM Doctor d LEFT JOIN d.patients p WHERE d.isGeneralPractitioner = true GROUP BY d ORDER BY COUNT(p) DESC")
    List<Object[]> findGPsWithPatientCount();
}
