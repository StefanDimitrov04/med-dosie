package com.example.csb869_medicinsko_dosie.service.impl;

import com.example.csb869_medicinsko_dosie.data.entity.Doctor;
import com.example.csb869_medicinsko_dosie.data.repo.DoctorRepository;
import com.example.csb869_medicinsko_dosie.dto.DoctorDTO;
import com.example.csb869_medicinsko_dosie.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class DoctorServiceImplTest {

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorServiceImpl doctorService;

    private Doctor doctor;

    @BeforeEach
    void init() {
        doctor = Doctor.builder()
                .identificationNumber("DOC001")
                .name("Д-р Иванов")
                .speciality("Общопрактикуващ лекар")
                .isGeneralPractitioner(true)
                .build();
        doctor.setId(1L);
    }

    @Test
    void getAllDoctors_returnsList() {
        given(doctorRepository.findAll()).willReturn(Arrays.asList(doctor));

        List<DoctorDTO> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Д-р Иванов");
    }

    @Test
    void getDoctorById_found() {
        given(doctorRepository.findById(1L)).willReturn(Optional.of(doctor));

        DoctorDTO result = doctorService.getDoctorById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getIdentificationNumber()).isEqualTo("DOC001");
    }

    @Test
    void getDoctorById_notFound_throwsException() {
        given(doctorRepository.findById(99L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(99L));
    }

    @Test
    void createDoctor_savesAndReturns() {
        DoctorDTO dto = DoctorDTO.builder()
                .identificationNumber("DOC002")
                .name("Д-р Петрова")
                .speciality("Кардиолог")
                .build();

        Doctor saved = Doctor.builder()
                .identificationNumber("DOC002")
                .name("Д-р Петрова")
                .speciality("Кардиолог")
                .build();
        saved.setId(2L);

        given(doctorRepository.save(any(Doctor.class))).willReturn(saved);

        DoctorDTO result = doctorService.createDoctor(dto);

        assertThat(result.getName()).isEqualTo("Д-р Петрова");
    }

    @Test
    void deleteDoctor_notFound_throwsException() {
        given(doctorRepository.existsById(99L)).willReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> doctorService.deleteDoctor(99L));
    }

    @Test
    void getGeneralPractitioners_returnsOnlyGPs() {
        given(doctorRepository.findByIsGeneralPractitionerTrue()).willReturn(List.of(doctor));

        List<DoctorDTO> result = doctorService.getGeneralPractitioners();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isGeneralPractitioner()).isTrue();
    }
}
