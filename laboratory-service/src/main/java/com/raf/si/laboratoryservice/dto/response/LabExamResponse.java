package com.raf.si.laboratoryservice.dto.response;

import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
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
public class LabExamResponse {

    private UUID lbp;

    private Date scheduledDate;

    private String note;

    private ExamStatus examStatus;
}
