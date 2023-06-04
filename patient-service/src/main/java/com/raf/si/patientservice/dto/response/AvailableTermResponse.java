package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.testing.Availability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableTermResponse {
    private Date dateAndTime;
    private UUID pbo;
    private Availability availability;
    private int availableNursesNum;
    private int scheduledTermsNum;
}
