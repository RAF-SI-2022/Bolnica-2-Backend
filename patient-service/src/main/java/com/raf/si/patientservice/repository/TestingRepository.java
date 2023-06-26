package com.raf.si.patientservice.repository;

import com.raf.si.patientservice.model.Testing;
import com.raf.si.patientservice.model.enums.testing.TestResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestingRepository extends JpaRepository<Testing, Long> {
    Page<Testing> findTestingByTestResult(TestResult testResult, Pageable pageable);
}
