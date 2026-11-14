package com.example.csb869_medicinsko_dosie.web.api;

import com.example.csb869_medicinsko_dosie.dto.DiagnosisDTO;
import com.example.csb869_medicinsko_dosie.dto.PatientDTO;
import com.example.csb869_medicinsko_dosie.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatisticsApiController {

    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final ExaminationService examinationService;
    private final SickLeaveService sickLeaveService;
    private final DoctorService doctorService;

    @GetMapping("/patients-by-diagnosis/{diagnosisId}")
    public List<PatientDTO> patientsByDiagnosis(@PathVariable Long diagnosisId) {
        return patientService.getPatientsByDiagnosis(diagnosisId);
    }

    @GetMapping("/most-common-diagnosis")
    public List<DiagnosisDTO> mostCommonDiagnosis() {
        return diagnosisService.getMostCommonDiagnosis();
    }

    @GetMapping("/patients-by-gp/{doctorId}")
    public List<PatientDTO> patientsByGP(@PathVariable Long doctorId) {
        return patientService.getPatientsByGeneralPractitioner(doctorId);
    }

    @GetMapping("/total-price-paid-by-patients")
    public BigDecimal totalPricePaidByPatients() {
        return examinationService.getTotalPricePaidByPatients();
    }

    @GetMapping("/price-paid-per-doctor")
    public List<Map<String, Object>> pricePaidPerDoctor() {
        return examinationService.getPricePaidByPatientsPerDoctor();
    }

    @GetMapping("/patients-count-per-gp")
    public List<Map<String, Object>> patientsCountPerGP() {
        return doctorService.getGPsWithPatientCount();
    }

    @GetMapping("/visits-per-doctor")
    public List<Map<String, Object>> visitsPerDoctor() {
        return examinationService.getVisitCountPerDoctor();
    }

    @GetMapping("/month-with-most-sick-leaves")
    public List<Map<String, Object>> monthWithMostSickLeaves() {
        return sickLeaveService.getMonthWithMostSickLeaves();
    }

    @GetMapping("/doctors-with-most-sick-leaves")
    public List<Map<String, Object>> doctorsWithMostSickLeaves() {
        return sickLeaveService.getDoctorsWithMostSickLeaves();
    }
}
