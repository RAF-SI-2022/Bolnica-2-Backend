package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.AvailableTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailableTermRepository extends JpaRepository<AvailableTerm, Long> {

    @Query(value = "select a from AvailableTerm a where a.pbo=:pbo" +
            " and a.dateAndTime>:startTime and a.dateAndTime<:endTime")
    List<AvailableTerm> findByDateAndTimeBetweenAndPbo(LocalDateTime startTime, LocalDateTime endTime, UUID pbo);

    @Query(value = "select a from AvailableTerm a where a.dateAndTime=:dateAndTime and a.pbo=:pbo")
    Optional<AvailableTerm> findByDateAndTimeAndPbo(LocalDateTime dateAndTime, UUID pbo);
}
