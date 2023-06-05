package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Testing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestingRepository extends JpaRepository<Testing, Long> {
}
