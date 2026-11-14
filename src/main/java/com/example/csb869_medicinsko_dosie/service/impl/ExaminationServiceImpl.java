package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.*;
import com.example.csb869_medicinsko_dosie.data.repo.*;
import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ExaminationDTO> getAllExaminations() {
        return examinationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExaminationDTO getExaminationById(Long id) {
        Examination exam = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination with id=" + id + " not found"));
        return toDTO(exam);
    }

    @Override
    public ExaminationDTO createExamination(ExaminationDTO dto) {
        Examination exam = buildFromDTO(new Examination(), dto);
        exam.setId(null);
        return toDTO(examinationRepository.save(exam));
    }

    @Override
    public ExaminationDTO updateExamination(Long id, ExaminationDTO dto) {
        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination with id=" + id + " not found"));
        buildFromDTO(existing, dto);
        return toDTO(examinationRepository.save(existing));
    }

    @Override
    public void deleteExamination(Long id) {
        Examination exam = examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination with id=" + id + " not found"));
        examinationRepository.delete(exam);
    }

    @Override
    public List<ExaminationDTO> getExaminationsByPatient(Long patientId) {
        return examinationRepository.findByPatientId(patientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExaminationDTO> getExaminationsByDoctor(Long doctorId) {
        return examinationRepository.findByDoctorId(doctorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExaminationDTO> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to) {
        List<Examination> results = (doctorId != null)
                ? examinationRepository.findByDoctorIdAndDateBetween(doctorId, from, to)
                : examinationRepository.findByDateBetween(from, to);
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalPricePaidByPatients() {
        BigDecimal total = examinationRepository.sumPricePaidByPatients();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public List<Map<String, Object>> getPricePaidByPatientsPerDoctor() {
        return examinationRepository.sumPricePaidByPatientsGroupedByDoctor().stream()
                .map(row -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    Doctor doctor = (Doctor) row[0];
                    entry.put("doctorId", doctor.getId());
                    entry.put("doctorName", doctor.getName());
                    entry.put("totalPrice", row[1]);
                    return entry;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getVisitCountPerDoctor() {
        return examinationRepository.countVisitsPerDoctor().stream()
                .map(row -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    Doctor doctor = (Doctor) row[0];
                    entry.put("doctorId", doctor.getId());
                    entry.put("doctorName", doctor.getName());
                    entry.put("visitCount", row[1]);
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private Examination buildFromDTO(Examination exam, ExaminationDTO dto) {
        exam.setDate(dto.getDate());
        exam.setTreatment(dto.getTreatment());
        exam.setPrice(dto.getPrice());

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        exam.setDoctor(doctor);

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        exam.setPatient(patient);

        if (dto.getDiagnosisIds() != null && !dto.getDiagnosisIds().isEmpty()) {
            List<Diagnosis> found = diagnosisRepository.findAllById(dto.getDiagnosisIds());
            if (found.size() != dto.getDiagnosisIds().size()) {
                throw new ResourceNotFoundException("One or more diagnosis IDs not found");
            }
            exam.setDiagnoses(new HashSet<>(found));
        } else {
            exam.setDiagnoses(new HashSet<>());
        }
        return exam;
    }

    private ExaminationDTO toDTO(Examination exam) {
        ExaminationDTO dto = new ExaminationDTO();
        dto.setId(exam.getId());
        dto.setDate(exam.getDate());
        dto.setTreatment(exam.getTreatment());
        dto.setPrice(exam.getPrice());
        if (exam.getDoctor() != null) {
            dto.setDoctorId(exam.getDoctor().getId());
            dto.setDoctorName(exam.getDoctor().getName());
        }
        if (exam.getPatient() != null) {
            dto.setPatientId(exam.getPatient().getId());
            dto.setPatientName(exam.getPatient().getName());
            dto.setPaidByPatient(!exam.getPatient().isHealthInsured());
        }
        if (exam.getDiagnoses() != null) {
            Set<DiagnosisDTO> diagDTOs = exam.getDiagnoses().stream()
                    .map(d -> modelMapper.map(d, DiagnosisDTO.class))
                    .collect(Collectors.toSet());
            dto.setDiagnoses(diagDTOs);
        }
        return dto;
    }
}
