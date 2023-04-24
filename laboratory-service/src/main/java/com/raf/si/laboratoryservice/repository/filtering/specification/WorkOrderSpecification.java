package com.raf.si.laboratoryservice.repository.filtering.specification;

import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.repository.filtering.filter.LabExamFilter;
import com.raf.si.laboratoryservice.repository.filtering.filter.WorkOrderFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class WorkOrderSpecification implements Specification<LabWorkOrder> {

    private final WorkOrderFilter filter;

    public WorkOrderSpecification(WorkOrderFilter filter) {
        this.filter = filter;
    }


    @Override
    public Predicate toPredicate(Root<LabWorkOrder> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<Date> creationDate = root.get("creationTime");
        Path<OrderStatus> orderStatus = root.get("orderStatus");
        Path<UUID> lbp = root.get("lbp");

        final List<Predicate> predicates = new ArrayList<>();
        if(filter.getLbp() != null) {
            predicates.add(criteriaBuilder.equal(lbp, filter.getLbp()));
        }
        if(filter.getOrderStatus() != null) {
            predicates.add(criteriaBuilder.equal(orderStatus, filter.getOrderStatus()));
        }

        if(filter.getStartDate() == null && filter.getEndDate() == null){
            Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            Date endDate = DateUtils.addDays(startDate, 1);
            predicates.add(criteriaBuilder.between(creationDate, startDate, endDate));
        }
        else if(filter.getStartDate() != null){
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(creationDate, filter.getStartDate()));
        }
        else if(filter.getEndDate() != null){
            predicates.add(criteriaBuilder.lessThanOrEqualTo(creationDate, filter.getEndDate()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
