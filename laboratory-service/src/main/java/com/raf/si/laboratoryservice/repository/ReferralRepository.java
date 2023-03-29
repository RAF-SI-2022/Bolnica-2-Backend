package com.raf.si.laboratoryservice.repository;

import com.raf.si.laboratoryservice.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
}
