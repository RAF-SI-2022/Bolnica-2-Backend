package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Vaccination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface VaccinationRepository  extends JpaRepository<Vaccination, Long> {
    Page<Vaccination> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    @Modifying
    @Query("update Vaccination v set v.deleted=true where v.healthRecord=:hr")
    Integer updateDeletedByHealthRecord(@PathVariable HealthRecord hr);
}
