package com.raf.si.patientservice.model;

import com.raf.si.patientservice.model.enums.patient.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jmbg;

    @Column(nullable = false, unique = true)
    private UUID lbp = UUID.randomUUID();

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String parentName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Date birthDate;

    @Column
    private Date deathDate;

    @Column(nullable = false)
    private String birthplace;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode citizenshipCountry;

    @Column
    private String address;

    @Column
    private String placeOfLiving;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode countryOfLiving;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String custodianJmbg;

    @Column
    private String custodianName;

    @Column
    @Enumerated(EnumType.STRING)
    private FamilyStatus familyStatus;

    @Column
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column
    private Integer childrenNum;

    @Column
    @Enumerated(EnumType.STRING)
    private Education education;

    @Column
    private String profession;

    @Column
    private Boolean deleted = false;

    @Column
    private Boolean immunized = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "health_record_id", referencedColumnName = "id")
    private HealthRecord healthRecord;
}
