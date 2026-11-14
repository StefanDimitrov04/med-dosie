package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.dto.DoctorDTO;
import com.example.csb869_medicinsko_dosie.service.DoctorService;
import com.example.csb869_medicinsko_dosie.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorApiController.class)
class DoctorApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"DOCTOR"})
    void getAllDoctors_returnsOk() throws Exception {
        DoctorDTO dto = DoctorDTO.builder()
                .id(1L)
                .name("Д-р Иванов")
                .identificationNumber("DOC001")
                .speciality("ОПЛ")
                .build();
        given(doctorService.getAllDoctors()).willReturn(List.of(dto));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Д-р Иванов"));
    }

    @Test
    void getAllDoctors_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createDoctor_asAdmin_returnsCreated() throws Exception {
        DoctorDTO dto = DoctorDTO.builder()
                .name("Д-р Нов")
                .identificationNumber("DOC003")
                .speciality("Хирург")
                .build();

        DoctorDTO saved = DoctorDTO.builder()
                .id(3L)
                .name("Д-р Нов")
                .identificationNumber("DOC003")
                .speciality("Хирург")
                .build();

        given(doctorService.createDoctor(any(DoctorDTO.class))).willReturn(saved);

        mockMvc.perform(post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"DOCTOR"})
    void createDoctor_asDoctor_returnsForbidden() throws Exception {
        DoctorDTO dto = DoctorDTO.builder()
                .name("Д-р Нов")
                .identificationNumber("DOC003")
                .speciality("Хирург")
                .build();

        mockMvc.perform(post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"DOCTOR"})
    void getDoctorById_returnsOk() throws Exception {
        DoctorDTO dto = DoctorDTO.builder()
                .id(1L)
                .name("Д-р Иванов")
                .identificationNumber("DOC001")
                .speciality("ОПЛ")
                .build();
        given(doctorService.getDoctorById(1L)).willReturn(dto);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificationNumber").value("DOC001"));
    }
}
