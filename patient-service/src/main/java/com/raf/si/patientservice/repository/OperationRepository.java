package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface OperationRepository extends CrudRepository<Operation, Long> {
    Page<Operation> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    @Modifying
    @Query("update Operation o set o.deleted=true where o.healthRecord=:hr")
    Integer updateDeletedByHealthRecord(@PathVariable HealthRecord hr);
}
