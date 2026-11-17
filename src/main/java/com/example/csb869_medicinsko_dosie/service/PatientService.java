package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.PatientDTO;

import java.util.List;

public interface PatientService {
    List<PatientDTO> getAllPatients();
    PatientDTO getPatientById(Long id);
    PatientDTO createPatient(PatientDTO patientDTO);
    PatientDTO updatePatient(Long id, PatientDTO patientDTO);
    void deletePatient(Long id);
    List<PatientDTO> getPatientsByGeneralPractitioner(Long doctorId);
    List<PatientDTO> getPatientsByDiagnosis(Long diagnosisId);
}
