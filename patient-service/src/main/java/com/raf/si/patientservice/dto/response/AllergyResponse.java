package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.Allergen;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllergyResponse {
    private Long id;
    private Allergen allergen;
    private Long healthRecordId;
}
