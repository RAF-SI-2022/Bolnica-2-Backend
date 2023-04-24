package com.raf.si.laboratoryservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.List;
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DepartmentResponseList {
    List<DepartmentResponse> departmentResponseList;
}
