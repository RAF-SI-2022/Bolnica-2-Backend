package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.HospitalRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface HospitalRoomRepository extends JpaRepository<HospitalRoom, Long> {
    Page<HospitalRoom> findByPbo(UUID pbo, Pageable pageable);
    @Query("SELECT SUM(r.capacity) FROM HospitalRoom r WHERE r.pbo = :pbo")
    int countTotalBedsByDepartment(@Param("pbo") UUID pbo);

    @Query("SELECT SUM(r.occupation) FROM HospitalRoom r WHERE r.pbo = :pbo")
    int countBedsInUseByDepartment(@Param("pbo") UUID pbo);

}
