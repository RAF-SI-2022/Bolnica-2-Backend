package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.Referral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Page<Referral> findByLbpAndCreationTimeBetweenAndDeletedFalse(UUID patientId, Timestamp dateFrom, Timestamp dateTo, Pageable pageable);
}
