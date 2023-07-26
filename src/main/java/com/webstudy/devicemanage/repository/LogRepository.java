package com.webstudy.devicemanage.repository;

import com.webstudy.devicemanage.model.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {

}
