package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
@Data
@AllArgsConstructor
public class VaccineResponse {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String producer;
}
