package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationListResponse {
    private List<Operation> operations;
    private long count;
}
