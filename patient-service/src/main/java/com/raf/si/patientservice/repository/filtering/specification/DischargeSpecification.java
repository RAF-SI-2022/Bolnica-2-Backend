package com.raf.si.patientservice.repository.filtering.specification;


import com.raf.si.patientservice.model.DischargeList;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.filtering.filter.DischargeFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DischargeSpecification implements Specification<DischargeList> {

    private final DischargeFilter filter;

    public DischargeSpecification(DischargeFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<DischargeList> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Join<Patient, DischargeList> patientConditionJoin = root.join("patient");
        Path<UUID> lbp = patientConditionJoin.get("lbp");
        Path<Date> date = root.get("date");

        final List<Predicate> predicates = new ArrayList<>();
        if (filter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        if (filter.getDateFrom() != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(date, filter.getDateFrom()));
        if (filter.getDateTo() != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(date, filter.getDateTo()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
