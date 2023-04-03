package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalExamination;
import com.raf.si.patientservice.repository.filtering.filter.MedicalExaminationFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicalExaminationSpecification implements Specification<MedicalExamination> {

    private final MedicalExaminationFilter filter;

    public MedicalExaminationSpecification(MedicalExaminationFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<MedicalExamination> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<HealthRecord> healthRecord = root.get("healthRecord");
        Path<Date> date = root.get("date");
        Path<Boolean> confidential = root.get("confidential");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(healthRecord, filter.getHealthRecord()));
        if(filter.getStartDate() != null)
            predicates.add(criteriaBuilder.between(date, filter.getStartDate(), filter.getEndDate()));
        if(filter.getCanGetConfidential() != null && filter.getCanGetConfidential() == false)
            predicates.add(criteriaBuilder.isFalse(confidential));

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
