package com.example.test_jwt.repository;

import com.example.test_jwt.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
@Transactional
public interface IUsersRepo extends JpaRepository<Users, Long> {
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findUsersByEmail(@Param("email") String email);

    @Query("SELECT u FROM Users u WHERE u.userName = :userName")
    Optional<Users> findUsersUserName(@Param("userName") String userName);

    Users findUsersByUserName(String user); // để sử dụng jwt

    @Query("UPDATE Users u SET u.isActive = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnabledActive(Long id, boolean enabled);

    @Query("SELECT u FROM Users u WHERE u.codeActivated = :code")
    Users findUsersByActive(@Param("code") String code);

    @Query("UPDATE Users u SET u.locked = ?2 WHERE u.userName = ?1")
    @Modifying
    void updateEnabledLocked(String userName, boolean enabled);
}