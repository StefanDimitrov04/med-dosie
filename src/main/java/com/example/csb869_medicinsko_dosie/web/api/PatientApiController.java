package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.data.entity.User;
import com.example.csb869_medicinsko_dosie.data.repo.UserRepository;
import com.example.csb869_medicinsko_dosie.dto.PatientDTO;
import com.example.csb869_medicinsko_dosie.service.PatientService;
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
@RequestMapping("/api/patients")
public class PatientApiController {

    private final PatientService patientService;
    private final UserRepository userRepository;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientDTO getPatient(@PathVariable Long id, Authentication auth) {
        if (isPatient(auth)) {
            Long myPatientId = getPatientIdForUser(auth.getName());
            if (!myPatientId.equals(id)) {
                throw new AccessDeniedException("Patients can only view their own data");
            }
        }
        return patientService.getPatientById(id);
    }

    @GetMapping("/by-gp/{doctorId}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<PatientDTO> getPatientsByGP(@PathVariable Long doctorId) {
        return patientService.getPatientsByGeneralPractitioner(doctorId);
    }

    @GetMapping("/by-diagnosis/{diagnosisId}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public List<PatientDTO> getPatientsByDiagnosis(@PathVariable Long diagnosisId) {
        return patientService.getPatientsByDiagnosis(diagnosisId);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(patientDTO));
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public PatientDTO updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        return patientService.updatePatient(id, patientDTO);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
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
