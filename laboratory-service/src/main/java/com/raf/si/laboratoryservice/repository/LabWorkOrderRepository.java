package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabWorkOrderRepository extends JpaRepository<LabWorkOrder, Long> {
    Page<LabWorkOrder> findByLbpAndCreationTimeBetweenAndStatusIsNot(UUID lbp, Date dateFrom,
                                                                     Date dateTo, OrderStatus status, Pageable pageable);
    Optional<LabWorkOrder> findById(Long id);

    Page<LabWorkOrder> findByLbpAndCreationTimeBetweenAndStatus(UUID lbp, Date dateFrom,
                                                                Date dateTo, OrderStatus status, Pageable pageable);
}
