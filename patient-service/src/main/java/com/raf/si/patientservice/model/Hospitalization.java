package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Hospitalization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "hospital_room_id", referencedColumnName = "id", nullable = false)
    private HospitalRoom hospitalRoom;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @Column(name = "lbz", nullable = false)
    private UUID doctorLBZ;
    @Column(name = "receipt_date", nullable = false)
    private Date receiptDate;
    @Column(nullable = false)
    private String diagnosis;
    private String note;
    @Column(name = "register_lbz", nullable = false)
    private UUID registerLbz;
    @Column(name = "discharge_date")
    private Date dischargeDate;
    @OneToOne(mappedBy = "hospitalization", fetch = FetchType.LAZY)
    private DischargeList dischargeList;
}
