package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.MedicalExamination;
import com.raf.si.patientservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.Date;
import java.util.List;

@Repository
public interface MedicalExaminationRepository extends JpaRepository<MedicalExamination, Long>, JpaSpecificationExecutor<MedicalExamination> {
    @Modifying
    @Query("update MedicalExamination m set m.deleted=true where m.healthRecord=:hr")
    Integer updateDeletedByHealthRecord(@PathVariable HealthRecord hr);
}
