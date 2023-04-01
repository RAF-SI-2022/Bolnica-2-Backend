package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.MedicalExamination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalExaminationListResponse {
    private List<MedicalExamination> examinations;
    private Long count;
}
