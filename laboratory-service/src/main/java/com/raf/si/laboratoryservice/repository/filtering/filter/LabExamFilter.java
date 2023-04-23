package com.raf.si.laboratoryservice.repository.filtering.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabExamFilter {
    private Date scheduledDate;
    private UUID lbp;
    private UUID pbo;
//    private Boolean includeDeleted;
}