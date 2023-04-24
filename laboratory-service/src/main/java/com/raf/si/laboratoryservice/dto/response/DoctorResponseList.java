package com.raf.si.laboratoryservice.dto.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DoctorResponseList {
    List<DoctorResponse> doctorResponseList;
}
