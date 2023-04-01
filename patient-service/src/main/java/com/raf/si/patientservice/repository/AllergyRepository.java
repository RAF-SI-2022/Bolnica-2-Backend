package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Allergy;
import com.raf.si.patientservice.model.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    Page<Allergy> findByHealthRecord(HealthRecord healthRecord, Pageable pageable);

    @Modifying
    @Query("update Allergy a set a.deleted=true where a.healthRecord=:hr")
    Integer updateDeletedByHealthRecord(@PathVariable HealthRecord hr);
}
