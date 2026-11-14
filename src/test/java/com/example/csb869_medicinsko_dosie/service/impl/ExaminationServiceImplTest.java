package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.*;
import com.example.csb869_medicinsko_dosie.data.repo.*;
import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class ExaminationServiceImplTest {

    @MockBean
    private ExaminationRepository examinationRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private ExaminationServiceImpl examinationService;

    private Doctor doctor;
    private Patient patient;
    private Examination examination;

    @BeforeEach
    void init() {
        doctor = Doctor.builder()
                .identificationNumber("DOC001")
                .name("Д-р Иванов")
                .speciality("ОПЛ")
                .isGeneralPractitioner(true)
                .build();
        doctor.setId(1L);

        patient = Patient.builder()
                .name("Георги Иванов")
                .egn("8501011234")
                .generalPractitioner(doctor)
                .healthInsured(true)
                .build();
        patient.setId(1L);

        examination = Examination.builder()
                .date(LocalDate.of(2025, 3, 10))
                .doctor(doctor)
                .patient(patient)
                .treatment("Почивка")
                .price(new BigDecimal("50.00"))
                .diagnoses(Set.of())
                .build();
        examination.setId(1L);
    }

    @Test
    void getExaminationById_found() {
        given(examinationRepository.findById(1L)).willReturn(Optional.of(examination));

        ExaminationDTO result = examinationService.getExaminationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getDoctorName()).isEqualTo("Д-р Иванов");
        assertThat(result.getPatientName()).isEqualTo("Георги Иванов");
        assertThat(result.isPaidByPatient()).isFalse();
    }

    @Test
    void getExaminationById_notFound_throwsException() {
        given(examinationRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> examinationService.getExaminationById(99L));
    }

    @Test
    void getExaminationsByPatient_returnsList() {
        given(examinationRepository.findByPatientId(1L)).willReturn(List.of(examination));

        List<ExaminationDTO> result = examinationService.getExaminationsByPatient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("50.00");
    }

    @Test
    void getTotalPricePaidByPatients_returnsZeroWhenNull() {
        given(examinationRepository.sumPricePaidByPatients()).willReturn(null);

        BigDecimal result = examinationService.getTotalPricePaidByPatients();

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getTotalPricePaidByPatients_returnsValue() {
        given(examinationRepository.sumPricePaidByPatients()).willReturn(new BigDecimal("150.00"));

        BigDecimal result = examinationService.getTotalPricePaidByPatients();

        assertThat(result).isEqualByComparingTo("150.00");
    }
}
