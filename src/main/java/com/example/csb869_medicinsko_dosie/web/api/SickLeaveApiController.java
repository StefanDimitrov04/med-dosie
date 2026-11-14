package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.data.entity.User;
import com.example.csb869_medicinsko_dosie.data.repo.UserRepository;
import com.example.csb869_medicinsko_dosie.dto.SickLeaveDTO;
import com.example.csb869_medicinsko_dosie.service.SickLeaveService;
import com.example.csb869_medicinsko_dosie.service.ExaminationService;
import com.example.csb869_medicinsko_dosie.dto.ExaminationDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sick-leaves")
public class SickLeaveApiController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;
    private final UserRepository userRepository;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<SickLeaveDTO> getAllSickLeaves() {
        return sickLeaveService.getAllSickLeaves();
    }

    @GetMapping("/{id}")
    public SickLeaveDTO getSickLeave(@PathVariable Long id, Authentication auth) {
        SickLeaveDTO sl = sickLeaveService.getSickLeaveById(id);
        if (isPatient(auth)) {
            Long myPatientId = getPatientIdForUser(auth.getName());
            ExaminationDTO exam = examinationService.getExaminationById(sl.getExaminationId());
            if (!exam.getPatientId().equals(myPatientId)) {
                throw new AccessDeniedException("Patients can only view their own sick leaves");
            }
        }
        return sl;
    }

    @GetMapping("/by-examination/{examinationId}")
    public List<SickLeaveDTO> getByExamination(@PathVariable Long examinationId, Authentication auth) {
        if (isPatient(auth)) {
            Long myPatientId = getPatientIdForUser(auth.getName());
            ExaminationDTO exam = examinationService.getExaminationById(examinationId);
            if (!exam.getPatientId().equals(myPatientId)) {
                throw new AccessDeniedException("Patients can only view their own sick leaves");
            }
        }
        return sickLeaveService.getSickLeavesByExamination(examinationId);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ResponseEntity<SickLeaveDTO> createSickLeave(@Valid @RequestBody SickLeaveDTO sickLeaveDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sickLeaveService.createSickLeave(sickLeaveDTO));
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public SickLeaveDTO updateSickLeave(@PathVariable Long id, @Valid @RequestBody SickLeaveDTO sickLeaveDTO) {
        return sickLeaveService.updateSickLeave(id, sickLeaveDTO);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSickLeave(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
    }

    private boolean isPatient(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
    }

    private Long getPatientIdForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (user.getPatient() == null) {
            throw new IllegalStateException("No patient profile linked to user: " + username);
        }
        return user.getPatient().getId();
    }
}
