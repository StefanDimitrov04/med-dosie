package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;

import java.util.List;

public interface DiagnosisService {
    List<DiagnosisDTO> getAllDiagnoses();
    DiagnosisDTO getDiagnosisById(Long id);
    DiagnosisDTO createDiagnosis(DiagnosisDTO diagnosisDTO);
    DiagnosisDTO updateDiagnosis(Long id, DiagnosisDTO diagnosisDTO);
    void deleteDiagnosis(Long id);
    List<DiagnosisDTO> getMostCommonDiagnosis();
}
