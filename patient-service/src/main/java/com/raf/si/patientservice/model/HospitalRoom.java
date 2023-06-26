package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
public class HospitalRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pbo", nullable = false)
    private UUID pbo;
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;
    @Column(name = "room_name")
    private String roomName;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Integer occupation;
    @Column(nullable = false)
    private Integer respirators;
    @Column
    private String description;
    @Column
    private Boolean covid = false;

    public void incrementOccupation() {
        occupation++;
    }

    public void decrementOccupation() {
        occupation--;
    }
}
