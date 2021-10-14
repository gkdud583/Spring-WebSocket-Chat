package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);





}