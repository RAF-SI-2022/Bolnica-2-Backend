package com.raf.si.userservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotNull(message = "Polje za token ne sme biti prazno")
    private UUID resetToken;
    @NotEmpty(message = "Polje za sifru ne sme biti prazno")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]*$", message = "Invalidan format sifre")
    private String password;
}
