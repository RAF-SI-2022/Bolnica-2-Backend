package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.patient.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class PatientResponse {
    private Long id;
    private String jmbg;
    private UUID lbp;
    private String firstName;
    private String parentName;
    private String lastName;
    private Gender gender;
    private Date birthDate;
    private Date deathDate;
    private String birthplace;
    private CountryCode citizenshipCountry;
    private String address;
    private String placeOfLiving;
    private CountryCode countryOfLiving;
    private String phoneNumber;
    private String email;
    private String custodianJmbg;
    private String custodianName;
    private FamilyStatus familyStatus;
    private MaritalStatus maritalStatus;
    private Integer childrenNum;
    private Education education;
    private String profession;
    private Boolean deleted;
    private Boolean immunized;
    private Long healthRecordId;
}
