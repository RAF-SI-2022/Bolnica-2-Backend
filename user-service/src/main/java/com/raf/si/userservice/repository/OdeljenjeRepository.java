package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Odeljenje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdeljenjeRepository extends JpaRepository<Odeljenje,Long> {
}
