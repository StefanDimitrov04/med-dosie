package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.SickLeaveDTO;

import java.util.List;
import java.util.Map;

public interface SickLeaveService {
    List<SickLeaveDTO> getAllSickLeaves();
    SickLeaveDTO getSickLeaveById(Long id);
    SickLeaveDTO createSickLeave(SickLeaveDTO sickLeaveDTO);
    SickLeaveDTO updateSickLeave(Long id, SickLeaveDTO sickLeaveDTO);
    void deleteSickLeave(Long id);
    List<SickLeaveDTO> getSickLeavesByExamination(Long examinationId);
    List<Map<String, Object>> getMonthWithMostSickLeaves();
    List<Map<String, Object>> getDoctorsWithMostSickLeaves();
}
