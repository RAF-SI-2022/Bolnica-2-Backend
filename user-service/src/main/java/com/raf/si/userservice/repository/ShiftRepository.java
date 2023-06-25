package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Shift;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query(value = "select count(s) from Shift s where s.user=:user and s.shiftType=:shiftType" +
            " and s.startTime>=:start and s.startTime<:end")
    long countShiftsByShiftTypeForUserBetweenDates(User user, LocalDateTime start, LocalDateTime end, ShiftType shiftType);

    List<Shift> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query(value = "select case when (count(s) > 0) then true else false end" +
            " from Shift s where s.user.lbz=:lbz and s.user.covidAccess=:covid" +
            " and s.startTime<=:start and s.endTime>=:end")
    Boolean canScheduleForLbz(UUID lbz, boolean covid, LocalDateTime start, LocalDateTime end);
}
