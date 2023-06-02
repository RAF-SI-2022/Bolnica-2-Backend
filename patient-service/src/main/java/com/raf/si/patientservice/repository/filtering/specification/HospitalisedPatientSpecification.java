package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.filtering.filter.HospitalisedPatientSearchFilter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
public class HospitalisedPatientSpecification implements Specification<Hospitalization> {

    private final HospitalisedPatientSearchFilter filter;

    public HospitalisedPatientSpecification(HospitalisedPatientSearchFilter filter) {
        this.filter = filter;
    }


    @Override
    public Predicate toPredicate(Root<Hospitalization> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Join<Patient, Hospitalization> patientsJoin = root.join("patient");
        Join<HospitalRoom, Hospitalization> roomJoin = root.join("hospitalRoom");
        Path<UUID> lbp = patientsJoin.get("lbp");
        Path<UUID> pbo = roomJoin.get("pbo");
        Path<String> firstName = patientsJoin.get("firstName");
        Path<String> lastName = patientsJoin.get("lastName");
        Path<String> jmbg = patientsJoin.get("jmbg");
        Path<Date> dischargeDate = root.get("dischargeDate");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isNull(dischargeDate));

        if(filter.getPbo() != null)
            predicates.add(criteriaBuilder.equal(pbo, filter.getPbo()));
        if(filter.getLbp() != null)
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        if(filter.getFirstName() != null)
            predicates.add(criteriaBuilder.like(firstName, "%" + filter.getFirstName() + "%"));
        if(filter.getLastName() != null)
            predicates.add(criteriaBuilder.like(lastName, "%" + filter.getLastName() + "%"));
        if(filter.getJmbg() != null)
            predicates.add(criteriaBuilder.equal(jmbg, filter.getJmbg()));
        if(filter.getDepartmentIds() != null)
            predicates.add(pbo.in(filter.getDepartmentIds()));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
