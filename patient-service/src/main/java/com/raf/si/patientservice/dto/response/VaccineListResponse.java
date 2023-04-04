package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class VaccineListResponse {
    private List<VaccineResponse> vaccines;
}
