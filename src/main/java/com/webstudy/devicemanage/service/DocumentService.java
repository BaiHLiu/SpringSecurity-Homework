package com.webstudy.devicemanage.service;

import com.webstudy.devicemanage.model.Document;
import com.webstudy.devicemanage.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public String getUuid(Integer documentId){
        Document document = documentRepository.getOne(documentId);
        return document.getUuid();
    }


}
