package com.raf.si.patientservice.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class PatientRequest {
    //lbp, id i obrisan
    @NotEmpty(message = "JMBG ne sme biti prazan")
    private String jmbg;
    @NotEmpty(message = "Ime ne sme biti prazno")
    private String firstName;
    @NotEmpty(message = "Ime roditelja ne sme biti prazno")
    private String parentName;
    @NotEmpty(message = "Prezime ne sme biti prazno")
    private String lastName;
    @NotEmpty(message = "Pol ne sme biti prazan")
    private String gender;
    @NotNull(message = "Datum rodjenja ne sme biti prazan")
    private Date birthDate;
    private Date deathDate;
    @NotEmpty(message = "Mesto rodjenja ne sme biti prazno")
    private String birthplace;
    @NotEmpty(message = "Zemlja drzavljanstva ne sme biti prazna")
    private String citizenshipCountry;
    private String address;
    private String placeOfLiving;
    @NotEmpty(message = "Zemlja stanovanja ne sme biti prazna")
    private String countryOfLiving;
    private String phoneNumber;
    @Email(message = "Neispravan format polja email")
    private String email;
    private String custodianJmbg;
    private String custodianName;
    private String familyStatus;
    private String maritalStatus;
    private Integer childrenNum;
    private String education;
    private String profession;
    private Boolean immunized;
}
