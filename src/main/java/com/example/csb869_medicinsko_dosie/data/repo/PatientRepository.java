package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEgn(String egn);
    List<Patient> findByGeneralPractitionerId(Long doctorId);

    @Query("SELECT DISTINCT p FROM Patient p JOIN p.examinations e JOIN e.diagnoses d WHERE d.id = :diagnosisId")
    List<Patient> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    @Query("SELECT DISTINCT p FROM Patient p JOIN p.examinations e JOIN e.diagnoses d WHERE d.name = :diagnosisName")
    List<Patient> findByDiagnosisName(@Param("diagnosisName") String diagnosisName);
}
