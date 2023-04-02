package com.raf.si.laboratoryservice.dto.request;

import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLabExamStatusRequest {
    @NotEmpty(message = "Polje id ne sme biti prazno")
    private Long id;
    @NotNull(message = "Polje status ne sme biti prazno")
    private ExamStatus status;
}
