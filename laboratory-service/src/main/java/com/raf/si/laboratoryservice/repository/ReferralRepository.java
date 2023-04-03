package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus.NEREALIZOVAN;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Page<Referral> findByLbpAndCreationTimeBetweenAndDeletedFalse(UUID patientId, Timestamp dateFrom, Timestamp dateTo, Pageable pageable);

    Optional<List<Referral>> findByLbpAndPboReferredToAndStatus(UUID lbp, UUID pboFromToken, ReferralStatus status);
}
