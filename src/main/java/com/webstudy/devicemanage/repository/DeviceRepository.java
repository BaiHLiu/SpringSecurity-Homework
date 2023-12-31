package com.webstudy.devicemanage.repository;

import com.webstudy.devicemanage.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findByName(String name);

    void deleteById(Integer id);
}
