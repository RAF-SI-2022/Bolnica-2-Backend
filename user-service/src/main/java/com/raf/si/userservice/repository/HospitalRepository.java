package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital,Long> {

    Optional<Hospital> findByPbb(UUID pbb);
}
