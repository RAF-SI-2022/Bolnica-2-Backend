package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Page<Referral> findByLbpAndCreationTimeBetweenAndDeletedFalse(UUID patientId, Date dateFrom, Date dateTo, Pageable pageable);
    @Query("SELECT r FROM Referral r WHERE r.lbp = :lbp AND r.pboReferredFrom = :pboFromToken AND r.status = :status AND r.deleted = false")
    List<Referral> findByLbpAndPboReferredFromAndStatus(@Param("lbp") UUID lbp, @Param("pboFromToken") UUID pboFromToken, @Param("status") ReferralStatus status);

}
