package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Doctor;
import com.example.csb869_medicinsko_dosie.data.entity.Patient;
import com.example.csb869_medicinsko_dosie.data.repo.DoctorRepository;
import com.example.csb869_medicinsko_dosie.data.repo.PatientRepository;
import com.example.csb869_medicinsko_dosie.dto.PatientDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id=" + id + " not found"));
        return toDTO(patient);
    }

    @Override
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Doctor gp = doctorRepository.findById(patientDTO.getGeneralPractitionerId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        Patient patient = new Patient();
        patient.setName(patientDTO.getName());
        patient.setEgn(patientDTO.getEgn());
        patient.setGeneralPractitioner(gp);
        patient.setHealthInsured(patientDTO.isHealthInsured());
        return toDTO(patientRepository.save(patient));
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id=" + id + " not found"));
        Doctor gp = doctorRepository.findById(patientDTO.getGeneralPractitionerId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        existing.setName(patientDTO.getName());
        existing.setEgn(patientDTO.getEgn());
        existing.setGeneralPractitioner(gp);
        existing.setHealthInsured(patientDTO.isHealthInsured());
        return toDTO(patientRepository.save(existing));
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with id=" + id + " not found"));
        patientRepository.delete(patient);
    }

    @Override
    public List<PatientDTO> getPatientsByGeneralPractitioner(Long doctorId) {
        return patientRepository.findByGeneralPractitionerId(doctorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientDTO> getPatientsByDiagnosis(Long diagnosisId) {
        return patientRepository.findDistinctByExaminationsDiagnosesId(diagnosisId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PatientDTO toDTO(Patient patient) {
        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        if (patient.getGeneralPractitioner() != null) {
            dto.setGeneralPractitionerId(patient.getGeneralPractitioner().getId());
            dto.setGeneralPractitionerName(patient.getGeneralPractitioner().getName());
        }
        return dto;
    }
}
