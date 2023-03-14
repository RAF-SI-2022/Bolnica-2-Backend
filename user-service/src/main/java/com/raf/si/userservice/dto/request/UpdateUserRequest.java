package com.raf.si.userservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    @NotEmpty(message = "Polje ime ne sme biti prazno")
    private String firstName;
    @NotEmpty(message = "Polje prezime ne sme biti prazno")
    private String lastName;
    @NotNull(message = "Polje datum rodjenja ne sme biti prazno")
    private Date dateOfBirth;
    @NotEmpty(message = "Polje pol ne sme biti prazno")
    private String gender;
    @NotEmpty(message = "Polje JMBG ne sme biti prazno")
    private String jmbg;
    @NotEmpty(message = "Polje adresa stanovanja ne sme biti prazno")
    private String residentialAddress;
    @NotEmpty(message = "Polje mesto ne sme biti prazno")
    private String placeOfLiving;
    private String phone;
    @NotEmpty(message = "Polje email ne sme biti prazno")
    @Email(message = "Invalidan format imejla")
    private String email;
    @NotEmpty(message = "Polje titula ne sme biti prazno")
    private String title;
    @NotEmpty(message = "Polje profesija ne sme biti prazno")
    private String profession;
    @NotEmpty(message = "Polje korisnicko ime ne sme biti prazno")
    private String username;
    private String oldPassword;
    @Pattern(regexp = "^[a-zA-Z0-9_.-]*$", message = "Invalidan format sifre")
    private String newPassword;
    @NotNull(message = "Polje id odeljenja ne sme biti prazno")
    private Long departmentId;
}
