package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByName(String name);

    @Query(value = """
        SELECT d.* FROM diagnoses d
        JOIN examination_diagnoses ed ON ed.diagnosis_id = d.id
        GROUP BY d.id
        HAVING COUNT(ed.examination_id) = (
            SELECT MAX(cnt) FROM (
                SELECT COUNT(ed2.examination_id) AS cnt
                FROM examination_diagnoses ed2
                GROUP BY ed2.diagnosis_id
            ) AS sub
        )
        """, nativeQuery = true)
    List<Diagnosis> findMostCommonDiagnosis();
}
