package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Diagnosis;
import com.example.csb869_medicinsko_dosie.data.repo.DiagnosisRepository;
import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class DiagnosisServiceImplTest {

    @MockBean
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private DiagnosisServiceImpl diagnosisService;

    private Diagnosis diagnosis;

    @BeforeEach
    void init() {
        diagnosis = Diagnosis.builder()
                .name("Грип")
                .description("Вирусна инфекция")
                .build();
        diagnosis.setId(1L);
    }

    @Test
    void getAllDiagnoses_returnsList() {
        given(diagnosisRepository.findAll()).willReturn(Arrays.asList(diagnosis));

        List<DiagnosisDTO> result = diagnosisService.getAllDiagnoses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Грип");
    }

    @Test
    void getDiagnosisById_found() {
        given(diagnosisRepository.findById(1L)).willReturn(Optional.of(diagnosis));

        DiagnosisDTO result = diagnosisService.getDiagnosisById(1L);

        assertThat(result.getDescription()).isEqualTo("Вирусна инфекция");
    }

    @Test
    void getDiagnosisById_notFound_throwsException() {
        given(diagnosisRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> diagnosisService.getDiagnosisById(99L));
    }

    @Test
    void getMostCommonDiagnosis_returnsList() {
        given(diagnosisRepository.findMostCommonDiagnosis()).willReturn(List.of(diagnosis));

        List<DiagnosisDTO> result = diagnosisService.getMostCommonDiagnosis();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Грип");
    }
}
