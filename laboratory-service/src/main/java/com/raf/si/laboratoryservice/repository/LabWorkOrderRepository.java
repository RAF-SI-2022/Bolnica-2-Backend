package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabWorkOrderRepository extends JpaRepository<LabWorkOrder, Long> {

    List<LabWorkOrder> findAll();

//    @Query("SELECT w.id FROM LabWorkOrder w WHERE w.referral.department.id = :departmentId AND w.status <> :status")
//    List<UUID> findIdsByReferralDepartmentIdAndNotStatus(@Param("departmentId") Long departmentId, @Param("status") OrderStatus status);

    LabWorkOrder findByReferral(Referral referral);
}
