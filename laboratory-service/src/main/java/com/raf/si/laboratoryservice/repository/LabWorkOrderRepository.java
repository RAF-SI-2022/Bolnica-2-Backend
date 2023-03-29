package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.LabWorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabWorkOrderRepository extends JpaRepository<LabWorkOrder, Long> {
}
