package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.data.entity.User;
import com.example.csb869_medicinsko_dosie.data.repo.UserRepository;
import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examinations")
public class ExaminationApiController {

    private final ExaminationService examinationService;
    private final UserRepository userRepository;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<ExaminationDTO> getAllExaminations() {
        return examinationService.getAllExaminations();
    }

    @GetMapping("/{id}")
    public ExaminationDTO getExamination(@PathVariable Long id, Authentication auth) {
        ExaminationDTO exam = examinationService.getExaminationById(id);
        if (isPatient(auth)) {
            Long patientId = getPatientIdForUser(auth.getName());
            if (!exam.getPatientId().equals(patientId)) {
                throw new AccessDeniedException("You can only view your own examinations");
            }
        }
        return exam;
    }

    @GetMapping("/by-patient/{patientId}")
    public List<ExaminationDTO> getByPatient(@PathVariable Long patientId, Authentication auth) {
        if (isPatient(auth)) {
            Long myId = getPatientIdForUser(auth.getName());
            if (!myId.equals(patientId)) {
                throw new AccessDeniedException("You can only view your own examinations");
            }
        }
        return examinationService.getExaminationsByPatient(patientId);
    }

    @GetMapping("/by-doctor/{doctorId}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<ExaminationDTO> getByDoctor(@PathVariable Long doctorId) {
        return examinationService.getExaminationsByDoctor(doctorId);
    }

    @GetMapping("/by-doctor-and-period")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<ExaminationDTO> getByDoctorAndPeriod(
            @RequestParam(required = false) Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return examinationService.getExaminationsByDoctorAndPeriod(doctorId, from, to);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ResponseEntity<ExaminationDTO> createExamination(
            @Valid @RequestBody ExaminationDTO examinationDTO,
            Authentication auth) {
        if (isDoctor(auth)) {
            Long myDoctorId = getDoctorIdForUser(auth.getName());
            examinationDTO.setDoctorId(myDoctorId);
        }
        ExaminationDTO created = examinationService.createExamination(examinationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ExaminationDTO updateExamination(
            @PathVariable Long id,
            @Valid @RequestBody ExaminationDTO examinationDTO,
            Authentication auth) {
        ExaminationDTO existing = examinationService.getExaminationById(id);
        if (isDoctor(auth)) {
            Long myDoctorId = getDoctorIdForUser(auth.getName());
            if (!existing.getDoctorId().equals(myDoctorId)) {
                throw new AccessDeniedException("Doctors can only edit their own examinations");
            }
            examinationDTO.setDoctorId(myDoctorId);
            // Doctor cannot reassign patient
            examinationDTO.setPatientId(existing.getPatientId());
        }
        return examinationService.updateExamination(id, examinationDTO);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExamination(@PathVariable Long id) {
        examinationService.deleteExamination(id);
    }

    private boolean isPatient(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
    }

    private boolean isDoctor(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
    }

    private Long getPatientIdForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (user.getPatient() == null) {
            throw new IllegalStateException("No patient profile linked to user: " + username);
        }
        return user.getPatient().getId();
    }

    private Long getDoctorIdForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (user.getDoctor() == null) {
            throw new IllegalStateException("No doctor profile linked to user: " + username);
        }
        return user.getDoctor().getId();
    }
}
