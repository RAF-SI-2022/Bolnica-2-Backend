package com.raf.si.userservice.dto.response;

import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Setter
public class ListUserResponse {

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Title title;
    private Profession profession;
    private String phone;
    private String email;
}
