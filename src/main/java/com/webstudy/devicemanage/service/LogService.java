package com.webstudy.devicemanage.service;

import com.webstudy.devicemanage.model.LogEntity;
import com.webstudy.devicemanage.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    @Autowired
    private LogRepository logRepository;

    public void saveLog(LogEntity logEntity) {
        logRepository.save(logEntity);
    }

    public List<LogEntity> getAll(){
        return logRepository.findAll();
    }
}