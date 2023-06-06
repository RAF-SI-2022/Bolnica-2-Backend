package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospitalizationRepository extends JpaRepository<Hospitalization, Long>, JpaSpecificationExecutor<Hospitalization> {

    @Query(value = "select case when (count(h) > 0) then true else false end " +
            " from Hospitalization h where h.patient=:patient and h.dischargeDate is null")
    boolean patientAlreadyHospitalized(Patient patient);

    Optional<Hospitalization> findByHospitalRoomAndPatientAndDischargeDateIsNull(HospitalRoom hospitalRoom, Patient patient);

    @Query(value = "select p from Hospitalization h left join h.patient p where p.lbp = :lbp and h.dischargeDate is null")
    Optional<Patient> getHospitalizedPatient(@PathVariable("lbp")UUID lbp);

    @Query(value = "select h from Hospitalization h join fetch h.patient p where p.lbp = :lbp and h.dischargeDate is null")
    Optional<Hospitalization> getHospitalizationByLbp(@PathVariable("lbp")UUID lbp);
}
