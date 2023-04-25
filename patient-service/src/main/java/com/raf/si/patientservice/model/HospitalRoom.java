package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class HospitalRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "department_id", nullable = false)
    private Long departmentId;
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;
    @Column(name = "room_name")
    private String roomName;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Integer occupation;
    private String description;
}
