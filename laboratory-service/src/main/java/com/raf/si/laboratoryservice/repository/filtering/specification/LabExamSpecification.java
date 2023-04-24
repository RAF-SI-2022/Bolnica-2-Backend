package com.raf.si.laboratoryservice.repository.filtering.specification;

import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.repository.filtering.filter.LabExamFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class LabExamSpecification implements Specification<ScheduledLabExam> {

    private final LabExamFilter filter;

    public LabExamSpecification(LabExamFilter labExamFilter) {
        this.filter = labExamFilter;
    }

    @Override
    public Predicate toPredicate(Root<ScheduledLabExam> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Date> date = root.get("scheduledDate");
        Path<UUID> lbp = root.get("lbp");

        Date startDate = filter.getScheduledDate();

        final List<Predicate> predicates = new ArrayList<>();

        if (filter.getLbp() != null) {
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        }

        if (filter.getScheduledDate() != null) {
            Date endDate = DateUtils.addDays(startDate, 1);
            predicates.add(criteriaBuilder.between(date, startDate, endDate));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}