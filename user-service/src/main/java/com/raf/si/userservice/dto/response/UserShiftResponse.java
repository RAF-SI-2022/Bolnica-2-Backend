package com.raf.si.userservice.dto.response;

import com.raf.si.userservice.model.Shift;
import lombok.Data;

import java.util.List;

@Data
public class UserShiftResponse {
    UserResponse userResponse;
    List<Shift> shifts;
    Long shiftCount;
}
