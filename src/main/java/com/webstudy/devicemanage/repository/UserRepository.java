package com.webstudy.devicemanage.repository;

import com.webstudy.devicemanage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByUsername(String username);

    User findByUsername(String username);

    void deleteByUsername(String username);
}
