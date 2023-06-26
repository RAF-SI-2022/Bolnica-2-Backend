package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Department;
import com.raf.si.userservice.model.User;
import com.raf.si.userservice.model.enums.ShiftType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByLbz(UUID lbz);

    @Query("select u from users u join fetch u.permissions p where u.lbz=:lbz")
    Optional<User> findByLbzAndFetchPermissions(UUID lbz);

    @Query(value = "select case when (count(u) > 0)  then true else false end" +
            " from users u where u.lbz = :lbz and u.isDeleted = :isDeleted")
    boolean userExists(@PathVariable("lbz") UUID lbz, @PathVariable("isDeleted") boolean isDeleted);

    @Query(value = "select u from users u where lower(u.firstName) like %:firstName% and lower(u.lastName) like %:lastName% " +
            "and u.isDeleted in :includeDeleted and u.covidAccess in :hasCovidAccess " +
            "and lower(u.department.name) like %:departmentName% and lower(u.department.hospital.fullName) like %:hospitalName%")
    Page<User> listAllUsers(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                            @PathVariable("departmentName") String departmentName, @PathVariable("hospitalName") String hospitalName,
                            @PathVariable("includeDeleted") List<Boolean> includeDeleted,
                            @PathVariable("hasCovidAccess") List<Boolean> hasCovidAccess, Pageable pageable);

    Optional<User> findByPasswordToken(UUID passwordToken);

    @Query(value = "select distinct u from users u left join u.permissions p where p.name in :permissions")
    List<User> getAllDoctors(@PathVariable("permissions") List<String> permissions);

    @Query(value = "select distinct u from users u left join u.permissions p where p.name in :permissions and u.department = :department")
    List<User> getAllDoctorsByDepartment(@PathVariable("permissions") List<String> permissions, @PathVariable("department") Department department);

    @Query(value = "select u from users u where u.lbz in (:lbzList)")
    List<User> findByLbzInList(@PathVariable("lbz") List<UUID> lbzList);

    @Query(value = "select distinct u from users u left join u.permissions p where p.name = :headPermission and u.department = :department")
    Optional<User> getHeadOfDepartment(@PathVariable("department") Department department, @PathVariable("headPermission") String headPermission);

    @Query(value = "select count(distinct u) from users u join u.permissions as p join u.shifts as s on s.user=u" +
            " where u.department.pbo=:pbo and u.covidAccess=true and p.name in :permissions" +
            " and s.startTime<=:start and s.endTime>=:end and s.shiftType!=:shiftType")
    long countCovidNursesByPboAndShiftInTimeSlot(UUID pbo, List<String> permissions, LocalDateTime start, LocalDateTime end, ShiftType shiftType);

    @Query(value = "select u from users u where u.department.pbo=:pbo")
    Page<User> findSubordinatesForHeadOfDepartment(UUID pbo, Pageable pageable);

    @Query(value = "select u from users u where u.department.hospital.pbb=:pbb")
    Page<User> findSubordinatesForAdmin(UUID pbb, Pageable pageable);

    @Query(value = "select u from users u left join u.permissions p where u.department.pbo=:pbo" +
            " and p.name in :permissions")
    Page<User> findSubordinatesForNurse(UUID pbo, List<String> permissions, Pageable pageable);
}
