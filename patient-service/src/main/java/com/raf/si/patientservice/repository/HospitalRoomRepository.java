package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HospitalRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HospitalRoomRepository extends JpaRepository<HospitalRoom, Long> {
    Page<HospitalRoom> findByPbo(UUID pbo, Pageable pageable);
}
