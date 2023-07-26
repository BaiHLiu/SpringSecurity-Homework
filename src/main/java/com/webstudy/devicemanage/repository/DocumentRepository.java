package com.webstudy.devicemanage.repository;

import com.webstudy.devicemanage.model.Device;
import com.webstudy.devicemanage.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface DocumentRepository extends JpaRepository<Document, Integer>{
    public List<Document> findAllByDeviceId(Integer id);

    void deleteById(Integer id);

}
