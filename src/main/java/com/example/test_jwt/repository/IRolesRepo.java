package com.example.test_jwt.repository;

import com.example.test_jwt.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolesRepo extends JpaRepository<Roles, Long> {
    Roles findRolesById(Long id);
}
