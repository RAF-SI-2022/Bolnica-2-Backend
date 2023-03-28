package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.MedicalHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MedicalHistoryListResponse {
    private List<MedicalHistory> history;
    private Long count;
}
