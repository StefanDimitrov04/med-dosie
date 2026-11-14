package com.example.csb869_medicinsko_dosie.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationDTO {
    private Long id;

    @NotNull(message = "Date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Doctor is required")
    private Long doctorId;

    private String doctorName;

    @NotNull(message = "Patient is required")
    private Long patientId;

    private String patientName;

    private Set<Long> diagnosisIds;

    private Set<DiagnosisDTO> diagnoses;

    @Size(max = 1000)
    private String treatment;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private boolean paidByPatient;
}
