package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Diagnosis;
import com.example.csb869_medicinsko_dosie.data.repo.DiagnosisRepository;
import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import com.example.csb869_medicinsko_dosie.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<DiagnosisDTO> getAllDiagnoses() {
        return diagnosisRepository.findAll().stream()
                .map(d -> modelMapper.map(d, DiagnosisDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DiagnosisDTO getDiagnosisById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis with id=" + id + " not found"));
        return modelMapper.map(diagnosis, DiagnosisDTO.class);
    }

    @Override
    public DiagnosisDTO createDiagnosis(DiagnosisDTO diagnosisDTO) {
        Diagnosis diagnosis = modelMapper.map(diagnosisDTO, Diagnosis.class);
        diagnosis.setId(null);
        return modelMapper.map(diagnosisRepository.save(diagnosis), DiagnosisDTO.class);
    }

    @Override
    public DiagnosisDTO updateDiagnosis(Long id, DiagnosisDTO diagnosisDTO) {
        Diagnosis existing = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis with id=" + id + " not found"));
        existing.setName(diagnosisDTO.getName());
        existing.setDescription(diagnosisDTO.getDescription());
        return modelMapper.map(diagnosisRepository.save(existing), DiagnosisDTO.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis with id=" + id + " not found"));
        diagnosisRepository.delete(diagnosis);
    }

    @Override
    public List<DiagnosisDTO> getMostCommonDiagnosis() {
        return diagnosisRepository.findMostCommonDiagnosis().stream()
                .map(d -> modelMapper.map(d, DiagnosisDTO.class))
                .collect(Collectors.toList());
    }
}
