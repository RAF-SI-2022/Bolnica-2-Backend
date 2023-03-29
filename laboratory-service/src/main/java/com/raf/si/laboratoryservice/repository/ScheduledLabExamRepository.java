package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.ScheduledLabExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledLabExamRepository extends JpaRepository<ScheduledLabExam, Long> {
}
