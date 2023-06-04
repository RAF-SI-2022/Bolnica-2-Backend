package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.testing.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableTermResponse {
    private Long id;
    private LocalDateTime dateAndTime;
    private UUID pbo;
    private Availability availability;
    private int availableNursesNum;
    private int scheduledTermsNum;
}
