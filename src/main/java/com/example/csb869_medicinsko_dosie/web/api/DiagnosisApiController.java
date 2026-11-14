package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnoses")
public class DiagnosisApiController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public List<DiagnosisDTO> getAllDiagnoses() {
        return diagnosisService.getAllDiagnoses();
    }

    @GetMapping("/{id}")
    public DiagnosisDTO getDiagnosis(@PathVariable Long id) {
        return diagnosisService.getDiagnosisById(id);
    }

    @GetMapping("/most-common")
    public List<DiagnosisDTO> getMostCommon() {
        return diagnosisService.getMostCommonDiagnosis();
    }

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ResponseEntity<DiagnosisDTO> createDiagnosis(@Valid @RequestBody DiagnosisDTO diagnosisDTO) {
        DiagnosisDTO created = diagnosisService.createDiagnosis(diagnosisDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public DiagnosisDTO updateDiagnosis(@PathVariable Long id, @Valid @RequestBody DiagnosisDTO diagnosisDTO) {
        return diagnosisService.updateDiagnosis(id, diagnosisDTO);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
    }
}
