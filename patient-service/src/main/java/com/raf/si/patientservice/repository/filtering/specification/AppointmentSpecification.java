package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.filtering.filter.AppointmentFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class AppointmentSpecification implements Specification<Appointment> {

    private final AppointmentFilter filter;

    public AppointmentSpecification(AppointmentFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Patient> patient = root.get("patient");
        Path<Date> date = root.get("receiptDate");
        Path<UUID> pbo = root.get("pbo");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(pbo, filter.getPbo()));
        if (filter.getPatient() != null) {
            predicates.add(criteriaBuilder.equal(patient, filter.getPatient()));
        }
        if (filter.getDate() != null) {
            Date startDate = DateUtils.truncate(filter.getDate(), Calendar.DAY_OF_MONTH);
            Date endDate = DateUtils.addDays(startDate, 1);
            predicates.add(criteriaBuilder.between(date, startDate, endDate));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
