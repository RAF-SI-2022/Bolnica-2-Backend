package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.Diagnosis;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalHistory;
import com.raf.si.patientservice.repository.filtering.filter.MedicalHistoryFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistorySpecification implements Specification<MedicalHistory> {

    private final MedicalHistoryFilter filter;

    public MedicalHistorySpecification(MedicalHistoryFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<MedicalHistory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
         Path<HealthRecord> healthRecord = root.get("healthRecord");
         Path<Boolean> confidential = root.get("confidential");

         Join<Diagnosis, MedicalHistory> diagnosisJoin = root.join("diagnosis");
         Path<String> diagnosisCode = diagnosisJoin.get("code");

         final List<Predicate> predicates = new ArrayList<>();
         predicates.add(criteriaBuilder.equal(healthRecord, filter.getHealthRecord()));
         if(filter.getDiagnosisCode() != null && !filter.getDiagnosisCode().trim().isEmpty())
             predicates.add(criteriaBuilder.equal(diagnosisCode, filter.getDiagnosisCode()));
         if(filter.getCanGetConfidential() != null && filter.getCanGetConfidential() == false)
             predicates.add(criteriaBuilder.isFalse(confidential));

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
