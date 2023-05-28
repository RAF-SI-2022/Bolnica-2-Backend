package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByLbz(UUID lbz);

    @Query(value = "select case when (count(u) > 0)  then true else false end" +
            " from users u where u.lbz = :lbz and u.isDeleted = :isDeleted")
    boolean userExists(@PathVariable("lbz") UUID lbz, @PathVariable("isDeleted") boolean isDeleted);

    @Query(value = "select u from users u where lower(u.firstName) like %:firstName% and lower(u.lastName) like %:lastName% " +
            "and u.isDeleted in :includeDeleted " +
            "and lower(u.department.name) like %:departmentName% and lower(u.department.hospital.fullName) like %:hospitalName%")
    Page<User> listAllUsers(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                            @PathVariable("departmentName") String departmentName, @PathVariable("hospitalName") String hospitalName,
                            @PathVariable("includeDeleted") List<Boolean> includeDeleted, Pageable pageable);

    Optional<User> findByPasswordToken(UUID passwordToken);

    @Query(value = "select distinct u from users u left join u.permissions p where p.name in :permissions")
    List<User> getAllDoctors(@PathVariable("permissions") List<String> permissions);

    @Query(value = "select distinct u from users u left join u.permissions p where p.name in :permissions and u.department = :department")
    List<User> getAllDoctorsByDepartment(@PathVariable("permissions") List<String> permissions, @PathVariable("department") Department department);

    @Query(value = "select u from users u where u.lbz in (:lbzList)")
    List<User> findByLbzInList(@PathVariable("lbz") List<UUID> lbzList);
}
