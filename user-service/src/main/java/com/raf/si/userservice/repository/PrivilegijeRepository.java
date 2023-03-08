package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Privilegije;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegijeRepository extends JpaRepository<Privilegije,Long> {
}
