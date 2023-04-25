package com.raf.si.laboratoryservice.dto.response;

import com.raf.si.laboratoryservice.model.enums.user.Profession;
import com.raf.si.laboratoryservice.model.enums.user.Title;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserResponse {

    private Long id;
    private UUID lbz;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;
    private String JMBG;
    private String residentalAddress;
    private String placeOfLiving;
    private String phone;
    private String email;
    private Title title;
    private Profession profession;
    private String username;
    private boolean isDeleted;
    private List<String> permissions;
}
