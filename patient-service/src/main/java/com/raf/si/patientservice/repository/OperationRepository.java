package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends CrudRepository<Operation, Long> {
    Page<Operation> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    List<Operation> findByHealthRecord(HealthRecord healthRecord);
}
