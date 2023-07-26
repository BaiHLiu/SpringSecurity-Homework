package com.webstudy.devicemanage.repository;

import com.webstudy.devicemanage.model.Device;
import com.webstudy.devicemanage.model.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Integer> {
    List<Repair> findAll();
    List<Repair> findAllByDevice(Device device);
    void deleteById(Integer id);
}
