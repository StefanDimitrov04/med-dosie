package com.example.csb869_medicinsko_dosie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeaveDTO {
    private Long id;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Duration days is required")
    @Min(value = 1, message = "Must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Examination is required")
    private Long examinationId;

    private String patientName;

    private String doctorName;
}
