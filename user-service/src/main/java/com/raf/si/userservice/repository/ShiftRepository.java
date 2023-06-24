package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Shift;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query(value = "select count(s) from Shift s where s.user=:user and s.shiftType=:shiftType and s.startTime>=:date")
    long countShiftsByShiftTypeForUserAfterDate(User user, LocalDateTime date, ShiftType shiftType);
}
