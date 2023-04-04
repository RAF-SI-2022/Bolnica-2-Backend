package com.raf.si.laboratoryservice.model;

import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class LabWorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date creationTime = new Date();

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEOBRADJEN;

    //FK
    @OneToOne(mappedBy = "labWorkOrder", fetch = FetchType.LAZY)
    private Referral referral;

    @OneToMany(mappedBy = "labWorkOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnalysisParameterResult> analysisParameterResults;

    @Column(nullable = false)
    private UUID lbp;

    @Column(nullable = false)
    private UUID lbzTechnician;

    @Column
    private UUID lbzBiochemist;
}
