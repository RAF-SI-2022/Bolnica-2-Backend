package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.MedicalHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalHistoryListResponse {
    private List<MedicalHistory> history;
    private Long count;
}
