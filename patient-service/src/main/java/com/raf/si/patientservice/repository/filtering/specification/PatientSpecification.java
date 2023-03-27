package com.raf.si.patientservice.repository.filtering.specification;


import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.filtering.filter.PatientSearchFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientSpecification implements Specification<Patient> {

    private final PatientSearchFilter filter;

    public PatientSpecification(PatientSearchFilter patientSearchFilter) {
        this.filter = patientSearchFilter;
    }

    @Override
    public Predicate toPredicate(Root<Patient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<UUID> lbp = root.get("lbp");
        Path<String> firstName = root.get("firstName");
        Path<String> lastName = root.get("lastName");
        Path<String> jmbg = root.get("jmbg");
        Path<Boolean> deleted = root.get("deleted");

        final List<Predicate> predicates = new ArrayList<>();
        if(filter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        if(filter.getFirstName() != null)
            predicates.add(criteriaBuilder.like(firstName, "%" + filter.getFirstName() + "%"));
        if(filter.getLastName() != null)
            predicates.add(criteriaBuilder.like(lastName, "%" + filter.getLastName() + "%"));
        if(filter.getJmbg() != null)
            predicates.add(criteriaBuilder.equal(jmbg, filter.getJmbg()));
        if(filter.getIncludeDeleted() == null || filter.getIncludeDeleted() == false)
            predicates.add(criteriaBuilder.isFalse(deleted));

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
