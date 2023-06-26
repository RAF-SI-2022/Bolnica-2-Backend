package com.raf.si.userservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Entity(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "days_off", nullable = false)
    private Integer daysOff;

    public static List<String> doctorPermissions = Arrays.asList(new String[] {
            "ROLE_DR_SPEC_ODELJENJA",
            "ROLE_DR_SPEC",
            "ROLE_DR_SPEC_POV"
    });

    public static List<String> nursePermissions = Arrays.asList(new String[] {
            "ROLE_MED_SESTRA",
            "ROLE_VISA_MED_SESTRA"
    });
}
