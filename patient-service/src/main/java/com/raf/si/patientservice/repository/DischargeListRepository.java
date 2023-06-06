package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.DischargeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DischargeListRepository extends JpaRepository<DischargeList, Long>, JpaSpecificationExecutor<DischargeList> {
}
