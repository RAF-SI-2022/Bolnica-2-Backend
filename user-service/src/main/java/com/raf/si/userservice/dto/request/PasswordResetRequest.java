package com.raf.si.userservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
@Setter
public class PasswordResetRequest {

    @NotEmpty(message = "Email polje ne sme biti prazno")
    @Email(message = "Invalidan format email-a")
    private String email;
}
