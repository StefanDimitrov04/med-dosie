package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findByPatientId(Long patientId);
    List<Examination> findByDoctorId(Long doctorId);

    List<Examination> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);
    List<Examination> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT SUM(e.price) FROM Examination e WHERE e.patient.healthInsured = false")
    BigDecimal sumPricePaidByPatients();

    @Query("""
        SELECT e.doctor, SUM(e.price) as total
        FROM Examination e
        WHERE e.patient.healthInsured = false
        GROUP BY e.doctor
        ORDER BY total DESC
        """)
    List<Object[]> sumPricePaidByPatientsGroupedByDoctor();

    @Query("""
        SELECT e.doctor, COUNT(e) as cnt
        FROM Examination e
        GROUP BY e.doctor
        ORDER BY cnt DESC
        """)
    List<Object[]> countVisitsPerDoctor();
}
