package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.ShiftTime;
import com.raf.si.userservice.model.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftTimeRepository extends JpaRepository<ShiftTime, Long> {

    ShiftTime findByShiftType(ShiftType shiftType);
}
