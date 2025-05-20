package com.example.MyBookshelf.repository;

import com.example.MyBookshelf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
