package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.*;
import com.example.csb869_medicinsko_dosie.data.repo.ExaminationRepository;
import com.example.csb869_medicinsko_dosie.data.repo.SickLeaveRepository;
import com.example.csb869_medicinsko_dosie.dto.SickLeaveDTO;
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
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class SickLeaveServiceImplTest {

    @MockBean
    private SickLeaveRepository sickLeaveRepository;

    @MockBean
    private ExaminationRepository examinationRepository;

    @Autowired
    private SickLeaveServiceImpl sickLeaveService;

    private SickLeave sickLeave;
    private Examination examination;

    @BeforeEach
    void init() {
        Doctor doctor = Doctor.builder()
                .identificationNumber("DOC001")
                .name("Д-р Иванов")
                .speciality("ОПЛ")
                .build();
        doctor.setId(1L);

        Patient patient = Patient.builder()
                .name("Мария Иванова")
                .egn("9001011234")
                .generalPractitioner(doctor)
                .healthInsured(false)
                .build();
        patient.setId(1L);

        examination = Examination.builder()
                .date(LocalDate.of(2025, 4, 1))
                .doctor(doctor)
                .patient(patient)
                .price(new BigDecimal("80.00"))
                .diagnoses(Set.of())
                .build();
        examination.setId(1L);

        sickLeave = SickLeave.builder()
                .startDate(LocalDate.of(2025, 4, 2))
                .durationDays(5)
                .examination(examination)
                .build();
        sickLeave.setId(1L);
    }

    @Test
    void getSickLeaveById_found() {
        given(sickLeaveRepository.findById(1L)).willReturn(Optional.of(sickLeave));

        SickLeaveDTO result = sickLeaveService.getSickLeaveById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getDurationDays()).isEqualTo(5);
        assertThat(result.getPatientName()).isEqualTo("Мария Иванова");
    }

    @Test
    void getSickLeaveById_notFound_throwsException() {
        given(sickLeaveRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sickLeaveService.getSickLeaveById(99L));
    }

    @Test
    void getAllSickLeaves_returnsList() {
        given(sickLeaveRepository.findAll()).willReturn(List.of(sickLeave));

        List<SickLeaveDTO> result = sickLeaveService.getAllSickLeaves();

        assertThat(result).hasSize(1);
    }

    @Test
    void getMonthWithMostSickLeaves_emptyWhenNoData() {
        given(sickLeaveRepository.findMonthWithMostSickLeaves()).willReturn(List.of());

        List<java.util.Map<String, Object>> result = sickLeaveService.getMonthWithMostSickLeaves();

        assertThat(result).isEmpty();
    }
}
