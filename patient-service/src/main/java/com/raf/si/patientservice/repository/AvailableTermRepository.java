package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.AvailableTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailableTermRepository extends JpaRepository<AvailableTerm, Long> {

    @Query(value = "select a from AvailableTerm a where a.pbo=:pbo and a.dateAndTime between :startTime and :endTime")
    List<AvailableTerm> findByDateAndTimeBetweenAndPbo(Date startTime, Date endTime, UUID pbo);

    @Query(value = "select a from AvailableTerm a where a.dateAndTime=:dateAndTime and a.pbo=:pbo")
    Optional<AvailableTerm> findByDateAndTimeAndPbo(Date dateAndTime, UUID pbo);
}
