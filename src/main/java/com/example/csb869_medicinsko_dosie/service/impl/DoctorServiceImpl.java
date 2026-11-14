package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Doctor;
import com.example.csb869_medicinsko_dosie.data.repo.DoctorRepository;
import com.example.csb869_medicinsko_dosie.dto.DoctorDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(d -> modelMapper.map(d, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id=" + id + " not found"));
        return modelMapper.map(doctor, DoctorDTO.class);
    }

    @Override
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = modelMapper.map(doctorDTO, Doctor.class);
        doctor.setId(null);
        return modelMapper.map(doctorRepository.save(doctor), DoctorDTO.class);
    }

    @Override
    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id=" + id + " not found"));
        existing.setName(doctorDTO.getName());
        existing.setIdentificationNumber(doctorDTO.getIdentificationNumber());
        existing.setSpeciality(doctorDTO.getSpeciality());
        existing.setGeneralPractitioner(doctorDTO.isGeneralPractitioner());
        return modelMapper.map(doctorRepository.save(existing), DoctorDTO.class);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with id=" + id + " not found"));
        doctorRepository.delete(doctor);
    }

    @Override
    public List<DoctorDTO> getGeneralPractitioners() {
        return doctorRepository.findByIsGeneralPractitionerTrue().stream()
                .map(d -> modelMapper.map(d, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getGPsWithPatientCount() {
        return doctorRepository.findGPsWithPatientCount().stream()
                .map(row -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    Doctor doctor = (Doctor) row[0];
                    entry.put("doctorId", doctor.getId());
                    entry.put("doctorName", doctor.getName());
                    entry.put("patientCount", row[1]);
                    return entry;
                })
                .collect(Collectors.toList());
    }
}
