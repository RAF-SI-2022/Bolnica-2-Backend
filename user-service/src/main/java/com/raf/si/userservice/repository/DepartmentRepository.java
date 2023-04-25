package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findDepartmentByHospital(Hospital hospital);

    Optional<Department> findDepartmentByPbo(UUID pbb);

    List<Department> findDepartmenstByName(String name);

    // For boostraping only
    Department findDepartmentByNameAndHospital_ShortName(String name, String hospitalShortName);

}
