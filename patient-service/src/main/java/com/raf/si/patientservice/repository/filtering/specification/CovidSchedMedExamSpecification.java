package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.filtering.filter.CovidSchedMedExamFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class CovidSchedMedExamSpecification implements Specification<ScheduledMedExamination> {

    private final CovidSchedMedExamFilter filter;

    public CovidSchedMedExamSpecification(CovidSchedMedExamFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<ScheduledMedExamination> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Patient> patient = root.get("patient");
        Path<Date> appointmentDate = root.get("appointmentDate");

        final List<Predicate> predicates = new ArrayList<>();
        if (filter.getPatient() != null) {
            predicates.add(criteriaBuilder.equal(patient, filter.getPatient()));
        }
        if (filter.getDate() != null) {
            Date startDate = DateUtils.truncate(filter.getDate(), Calendar.DAY_OF_MONTH);
            Date endDate = DateUtils.addDays(startDate, 1);
            predicates.add(criteriaBuilder.between(appointmentDate, startDate, endDate));
        } else {
            Date currDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(appointmentDate, currDate));
        }

        query.orderBy(criteriaBuilder.asc(appointmentDate));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
