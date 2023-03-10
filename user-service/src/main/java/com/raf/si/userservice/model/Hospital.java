package com.raf.si.userservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private UUID pbb;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "short_name", nullable = false)
    private String shortName;
    @Column(nullable = false)
    private String place;
    @Column(nullable = false)
    private String address;
    @Column(name = "date_of_establishment", nullable = false)
    private Date dateOfEstablishment;
    @Column(nullable = false)
    private String activity;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
