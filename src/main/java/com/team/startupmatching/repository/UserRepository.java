package com.team.startupmatching.repository;

import com.team.startupmatching.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}