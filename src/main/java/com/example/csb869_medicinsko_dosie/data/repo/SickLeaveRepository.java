package com.example.csb869_medicinsko_dosie.data.repo;

import com.example.csb869_medicinsko_dosie.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    List<SickLeave> findByExaminationId(Long examinationId);

    @Query("""
        SELECT MONTH(s.startDate) as month, YEAR(s.startDate) as year, COUNT(s) as cnt
        FROM SickLeave s
        GROUP BY YEAR(s.startDate), MONTH(s.startDate)
        ORDER BY cnt DESC
        """)
    List<Object[]> findMonthWithMostSickLeaves();

    @Query("""
        SELECT e.doctor, COUNT(s) as cnt
        FROM SickLeave s JOIN s.examination e
        GROUP BY e.doctor
        ORDER BY cnt DESC
        """)
    List<Object[]> findDoctorsWithMostSickLeaves();
}
