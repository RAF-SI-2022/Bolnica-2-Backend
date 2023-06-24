package com.raf.si.userservice.dto.response;

import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
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
public class UserListResponse {

    private Long id;
    private UUID lbz;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Title title;
    private Profession profession;
    private String phone;
    private String email;
    private String departmentName;
    private String hospitalName;
    private boolean covidAccess;
    private int daysOff;
    private int usedDaysOff;
}
