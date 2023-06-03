package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.AvailableTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableTermRepository extends JpaRepository<AvailableTerm, Long> {

    @Query(value = "select a from AvailableTerm a where a.dateAndTime between :startTime and :endTime")
    List<AvailableTerm> findByDateAndTimeBetween(Date startTime, Date endTime);
}
