package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.MedicalReport;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.filtering.filter.MedicalReportFilter;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MedicalReportSpecification implements Specification<MedicalReport> {

    private final MedicalReportFilter filter;

    public MedicalReportSpecification(MedicalReportFilter filter) {
        this.filter = filter;
    }


    @Override
    public Predicate toPredicate(Root<MedicalReport> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        Join<Patient, MedicalReport> patientConditionJoin = root.join("patient");
        Path<UUID> lbp = patientConditionJoin.get("lbp");
        Path<Date> date = root.get("date");
        Path<Boolean> confidentIndicator = root.get("confidentIndicator");
        Path<Boolean> isDeleted = root.get("isDeleted");
        Path<String> diagnosis = root.get("diagnosis");

        final List<Predicate> predicates = new ArrayList<>();
        if (filter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        if (filter.getFrom() != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(date, filter.getFrom()));
        if (filter.getTo() != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(date, filter.getTo()));
        if (!filter.isConfidential()) {
            predicates.add(criteriaBuilder.equal(confidentIndicator, false));
        }
        predicates.add(criteriaBuilder.equal(isDeleted, false));
        if(filter.getDiagnosis() != null)
            predicates.add(criteriaBuilder.like(diagnosis, "%" + filter.getDiagnosis() + "%"));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
