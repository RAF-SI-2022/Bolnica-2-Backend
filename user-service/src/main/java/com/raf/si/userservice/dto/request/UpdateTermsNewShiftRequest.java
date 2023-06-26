package com.raf.si.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTermsNewShiftRequest {
    private TimeRequest oldShift;
    private TimeRequest newShift;
}
