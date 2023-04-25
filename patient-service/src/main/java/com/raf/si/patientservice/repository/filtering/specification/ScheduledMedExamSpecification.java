package com.raf.si.patientservice.repository.filtering.specification;


import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledMedExamFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class ScheduledMedExamSpecification implements Specification<ScheduledMedExamination> {

    private final ScheduledMedExamFilter filter;

    public ScheduledMedExamSpecification(ScheduledMedExamFilter scheduledMedExamFilter) {
        this.filter = scheduledMedExamFilter;
    }


    @Override
    public Predicate toPredicate(Root<ScheduledMedExamination> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<UUID> lbzDoctor= root.get("lbzDoctor");
        Path<Date> appointmentDate= root.get("appointmentDate");

        final List<Predicate> predicates = new ArrayList<>();
        if(filter.getLbz() != null)
            predicates.add(criteriaBuilder.equal(lbzDoctor, filter.getLbz()));
        if(filter.getAppointmentDate()!= null){
            /**
             * Very Hacky way to get schedMedExams for given day.
             */
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(filter.getAppointmentDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = calendar.getTime();

            predicates.add(criteriaBuilder.between(appointmentDate,filter.getAppointmentDate(),endDate));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}

