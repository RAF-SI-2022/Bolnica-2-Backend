package com.raf.si.laboratoryservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ExaminationResponse {

    private UUID lbp;

    private Date scheduledDate;

    private String note;
}
