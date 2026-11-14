package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExaminationService {
    List<ExaminationDTO> getAllExaminations();
    ExaminationDTO getExaminationById(Long id);
    ExaminationDTO createExamination(ExaminationDTO examinationDTO);
    ExaminationDTO updateExamination(Long id, ExaminationDTO examinationDTO);
    void deleteExamination(Long id);
    List<ExaminationDTO> getExaminationsByPatient(Long patientId);
    List<ExaminationDTO> getExaminationsByDoctor(Long doctorId);
    List<ExaminationDTO> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to);
    BigDecimal getTotalPricePaidByPatients();
    List<Map<String, Object>> getPricePaidByPatientsPerDoctor();
    List<Map<String, Object>> getVisitCountPerDoctor();
}
