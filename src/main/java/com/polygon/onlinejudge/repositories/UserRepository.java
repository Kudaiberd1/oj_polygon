package com.polygon.onlinejudge.repositories;

import com.polygon.onlinejudge.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}
