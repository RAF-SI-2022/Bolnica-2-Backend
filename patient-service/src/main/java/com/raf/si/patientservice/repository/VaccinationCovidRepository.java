package com.raf.si.patientservice.repository;


import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.VaccinationCovid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaccinationCovidRepository extends JpaRepository<VaccinationCovid, Long> {

    List<VaccinationCovid> findByHealthRecord_Patient(Patient patient);

    @Query("select v from VaccinationCovid v left join v.healthRecord h left join" +
            " h.patient p where p.lbp = :lbp")
    List<VaccinationCovid> getHistoryByLbp(UUID lbp);
}
