package com.raf.si.patientservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    @Column(name = "register_lbz", nullable = false)
    private UUID registerLbz;
    @Column(name = "visit_date", nullable = false)
    private Date visitDate = new Date();
    @Column(name = "visitor_first_name", nullable = false)
    private String visitorFirstName;
    @Column(name = "visitor_last_name", nullable = false)
    private String visitorLastName;
    @Column(name = "jmbg_visitor", nullable = false)
    private String JMBGVisitor;
    private String note;
}
