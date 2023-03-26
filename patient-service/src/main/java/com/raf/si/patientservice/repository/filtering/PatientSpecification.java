package com.raf.si.patientservice.repository.filtering;


import com.raf.si.patientservice.model.Patient;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PatientSpecification implements Specification<Patient> {

    private final PatientSearchFilter patientSearchFilter;

    public PatientSpecification(PatientSearchFilter patientSearchFilter) {
        this.patientSearchFilter = patientSearchFilter;
    }

    @Override
    public Predicate toPredicate(Root<Patient> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<UUID> lbp = root.get("lbp");
        Path<String> firstName = root.get("firstName");
        Path<String> lastName = root.get("lastName");
        Path<String> jmbg = root.get("jmbg");

        final List<Predicate> predicates = new ArrayList<>();
        if(patientSearchFilter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, patientSearchFilter.getLbp()));
        if(patientSearchFilter.getFirstName() != null)
            predicates.add(criteriaBuilder.like(firstName, "%" + patientSearchFilter.getFirstName() + "%"));
        if(patientSearchFilter.getLastName() != null)
            predicates.add(criteriaBuilder.like(lastName, "%" + patientSearchFilter.getLastName() + "%"));
        if(patientSearchFilter.getJmbg() != null)
            predicates.equals(criteriaBuilder.like(jmbg, patientSearchFilter.getJmbg()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
