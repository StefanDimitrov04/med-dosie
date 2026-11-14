package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Examination;
import com.example.csb869_medicinsko_dosie.data.entity.SickLeave;
import com.example.csb869_medicinsko_dosie.data.entity.Doctor;
import com.example.csb869_medicinsko_dosie.data.repo.ExaminationRepository;
import com.example.csb869_medicinsko_dosie.data.repo.SickLeaveRepository;
import com.example.csb869_medicinsko_dosie.dto.SickLeaveDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExaminationRepository examinationRepository;

    @Override
    public List<SickLeaveDTO> getAllSickLeaves() {
        return sickLeaveRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SickLeaveDTO getSickLeaveById(Long id) {
        SickLeave sl = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave with id=" + id + " not found"));
        return toDTO(sl);
    }

    @Override
    public SickLeaveDTO createSickLeave(SickLeaveDTO dto) {
        Examination exam = examinationRepository.findById(dto.getExaminationId())
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found"));
        SickLeave sl = SickLeave.builder()
                .startDate(dto.getStartDate())
                .durationDays(dto.getDurationDays())
                .examination(exam)
                .build();
        return toDTO(sickLeaveRepository.save(sl));
    }

    @Override
    public SickLeaveDTO updateSickLeave(Long id, SickLeaveDTO dto) {
        SickLeave existing = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave with id=" + id + " not found"));
        Examination exam = examinationRepository.findById(dto.getExaminationId())
                .orElseThrow(() -> new ResourceNotFoundException("Examination not found"));
        existing.setStartDate(dto.getStartDate());
        existing.setDurationDays(dto.getDurationDays());
        existing.setExamination(exam);
        return toDTO(sickLeaveRepository.save(existing));
    }

    @Override
    public void deleteSickLeave(Long id) {
        SickLeave sl = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave with id=" + id + " not found"));
        sickLeaveRepository.delete(sl);
    }

    @Override
    public List<SickLeaveDTO> getSickLeavesByExamination(Long examinationId) {
        return sickLeaveRepository.findByExaminationId(examinationId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getMonthWithMostSickLeaves() {
        return sickLeaveRepository.findMonthWithMostSickLeaves().stream()
                .limit(1)
                .map(row -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("month", row[0]);
                    entry.put("year", row[1]);
                    entry.put("count", row[2]);
                    return entry;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getDoctorsWithMostSickLeaves() {
        List<Object[]> rows = sickLeaveRepository.findDoctorsWithMostSickLeaves();
        if (rows.isEmpty()) return List.of();
        long maxCount = ((Number) rows.get(0)[1]).longValue();
        return rows.stream()
                .filter(row -> ((Number) row[1]).longValue() == maxCount)
                .map(row -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    Doctor doctor = (Doctor) row[0];
                    entry.put("doctorId", doctor.getId());
                    entry.put("doctorName", doctor.getName());
                    entry.put("count", row[1]);
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private SickLeaveDTO toDTO(SickLeave sl) {
        SickLeaveDTO dto = new SickLeaveDTO();
        dto.setId(sl.getId());
        dto.setStartDate(sl.getStartDate());
        dto.setDurationDays(sl.getDurationDays());
        if (sl.getExamination() != null) {
            dto.setExaminationId(sl.getExamination().getId());
            if (sl.getExamination().getPatient() != null) {
                dto.setPatientName(sl.getExamination().getPatient().getName());
            }
            if (sl.getExamination().getDoctor() != null) {
                dto.setDoctorName(sl.getExamination().getDoctor().getName());
            }
        }
        return dto;
    }
}
