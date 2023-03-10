package com.raf.si.userservice.repository;

import com.raf.si.userservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionsRepository extends JpaRepository<Permission,Long> {

    List<Permission> findPermissionsByNameIsIn(List<String> permissions);
}
