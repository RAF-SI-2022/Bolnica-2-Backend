package com.raf.si.laboratoryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReferralType type;

    @Column(nullable = false)
    private Date creationTime = new Date();

    @Column
    @Enumerated(EnumType.STRING)
    private ReferralStatus status = ReferralStatus.NEREALIZOVAN;

    @Column
    private String requiredAnalysis;

    @Column
    private String comment;

    @Column
    private String referralDiagnosis;

    @Column
    private String referralReason;

    //FK
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_work_order_id", referencedColumnName = "id")
    private LabWorkOrder labWorkOrder;

    @OneToMany(mappedBy = "parameter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AnalysisParameter> analysisParameters;

    @Column(nullable = false)
    private UUID lbz;

    @Column(nullable = false)
    private UUID pboReferredFrom;

    @Column(nullable = false)
    private UUID pboReferredTo;

    @Column(nullable = false)
    private UUID lbp;
}
