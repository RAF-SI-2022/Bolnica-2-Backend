package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Ustanova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UstanovaRepository extends JpaRepository<Ustanova,Long> {
}
