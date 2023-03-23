package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Operation;
import org.springframework.data.repository.CrudRepository;

public interface OperationRepository extends CrudRepository<Operation, Long> {
}
