package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.PatientCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientConditionRepository extends JpaRepository<PatientCondition, Long>,
        JpaSpecificationExecutor<PatientCondition> {

}
