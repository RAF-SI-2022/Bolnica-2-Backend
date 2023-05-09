package com.raf.si.patientservice.repository.filtering;

import com.raf.si.patientservice.model.HospitalRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HospitalRoomRepository extends JpaRepository<HospitalRoom, Long> {
    Page<HospitalRoom> findByPbo(UUID pbo, Pageable pageable);
}
