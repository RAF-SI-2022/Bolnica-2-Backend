package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.PatientCondition;
import com.raf.si.patientservice.repository.filtering.filter.PatientConditionFilter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PatientConditionSpecification implements Specification<PatientCondition> {

    private final PatientConditionFilter filter;

    @Override
    public Predicate toPredicate(Root<PatientCondition> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        Join<Patient, PatientCondition> patientConditionJoin = root.join("patient");
        Path<UUID> lbp = patientConditionJoin.get("lbp");
        Path<Date> collectedInfoDate = root.get("collectedInfoDate");

        final List<Predicate> predicates = new ArrayList<>();
        if (filter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        if (filter.getDateFrom() != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(collectedInfoDate, filter.getDateFrom()));
        if (filter.getDateTo() != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(collectedInfoDate, filter.getDateTo()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
