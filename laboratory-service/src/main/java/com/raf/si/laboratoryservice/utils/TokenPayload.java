package com.raf.si.laboratoryservice.utils;

import com.raf.si.laboratoryservice.model.enums.user.Profession;
import com.raf.si.laboratoryservice.model.enums.user.Title;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TokenPayload {

    private UUID lbz;
    private String firstName;
    private String lastName;
    private Title title;
    private Profession profession;
    private UUID pbo;
    private String departmentName;
    private UUID pbb;
    private String hospitalName;
    private List<String> permissions;
}
