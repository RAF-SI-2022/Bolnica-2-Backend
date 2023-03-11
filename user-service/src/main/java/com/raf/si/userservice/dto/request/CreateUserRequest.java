package com.raf.si.userservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotEmpty(message = "Polje ime ne sme biti prazno")
    private String firstName;
    @NotEmpty(message = "Polje prezime ne sme biti prazno")
    private String lastName;
    @NotNull(message = "Polje datum rodjenja je obavezno")
    private Date dateOfBirth;
    @NotEmpty(message = "Polje pol ne sme biti prazno")
    private String gender;
    @NotEmpty(message = "Polje JMBG ne sme biti prazno")
    private String jmbg;
    @NotEmpty(message = "Polje adresa stanovanja ne sme biti prazno")
    private String residentialAddress;
    @NotEmpty(message = "Polje mesto stanovanja ne sme biti prazno")
    private String placeOfLiving;
    private String phone;
    @NotEmpty(message = "Polje email ne sme biti prazno")
    @Email(message = "Neispravan format polja email")
    private String email;
    @NotEmpty(message = "Polje titula ne sme biti prazno")
    private String title;
    @NotEmpty(message = "Polje zanimanje ne sme biti prazno")
    private String profession;
    @NotNull(message = "Polje id odeljenja je obavezno")
    private Long departmentId;
    @NotNull(message = "Privilegije su obavezne")
    private String[] permissions;
}
