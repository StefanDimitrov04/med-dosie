package com.example.csb869_medicinsko_dosie.service;

import com.example.csb869_medicinsko_dosie.dto.DoctorDTO;

import java.util.List;
import java.util.Map;

public interface DoctorService {
    List<DoctorDTO> getAllDoctors();
    DoctorDTO getDoctorById(Long id);
    DoctorDTO createDoctor(DoctorDTO doctorDTO);
    DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO);
    void deleteDoctor(Long id);
    List<DoctorDTO> getGeneralPractitioners();
    List<Map<String, Object>> getGPsWithPatientCount();
}
