package com.webstudy.devicemanage.service;

import com.webstudy.devicemanage.dto.*;
import com.webstudy.devicemanage.exception.CustomException;
import com.webstudy.devicemanage.model.*;
import com.webstudy.devicemanage.repository.*;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private MaintainRepository maintainRepository;

    @Autowired
    private DocumentRepository documentRepository;


    public List<Device> getAllDevice() {
        return deviceRepository.findAll();
    }

    public Device getDevice(Integer id) {
        return deviceRepository.findById(id).get();
    }

    public Device addDevice(DeviceAddDTO dev) {
        Device device = new Device(dev.getName(), dev.getPosition(), dev.getMaintenanceCycle());
        deviceRepository.save(device);
        return device;
    }

    public void deleteDevice(Integer deviceId) {
        deviceRepository.deleteById(deviceId);
    }

    public Device modifyDevice(Device device) {
        deviceRepository.save(device);
        return device;

    }

    public Repair addRepair(Integer deviceId, RepairDTO rep) {
        Device device = deviceRepository.findById(deviceId).get();
        Repair repair = new Repair(device, rep.getHappenTime(), rep.getCause(), rep.getScheduledTime());
        repairRepository.save(repair);
        device.getRepairs().add(repair);
        return repair;
    }

    public void deleteRepair(Repair repair) {
        repairRepository.deleteById(repair.getId());
    }

    public Repair finishRepair(Integer repairId, FinishedDTO rep, String execName) {
        Repair repair = repairRepository.findById(repairId).get();
        repair.setExecutor(execName);
        repair.setFinished(true);
        repair.setFinishedTime(Timestamp.from(Instant.ofEpochMilli(rep.getFinishedTime())));
        repairRepository.save(repair);
        return repair;
    }

    public List<Repair> getAllRepair() {
        return repairRepository.findAll();
    }

    public Maintain addMaintain(Integer deviceId, MaintainDTO maint) {
        Device device = deviceRepository.findById(deviceId).get();
        Maintain maintain = new Maintain(device, maint.getScheduledTime());
        maintainRepository.save(maintain);
        device.getMaintains().add(maintain);
        return maintain;
    }

    public Maintain finishMaintain(Integer maintId, FinishedDTO maint) {
        Maintain maintain = maintainRepository.findById(maintId).get();
        //TODO:修改完成人
        maintain.setExecutor("default");
        maintain.setFinished(true);
        maintain.setFinishedTime(Timestamp.from(Instant.ofEpochMilli(maint.getFinishedTime())));
        maintainRepository.save(maintain);
        return maintain;
    }

    public Document addDocument(Integer deviceId, DocumentDTO doc, User uploadUser){
        Device device = deviceRepository.findById(deviceId).get();
        Document document = new Document(device, doc.getUuid(), doc.getFileName(), uploadUser);
        documentRepository.save(document);
        device.getDocuments().add(document);
        return document;
    }

    public Document getDocumentDetails(Integer documentId){
        return documentRepository.findById(documentId).get();
    }

    public void deleteDocument(Document document, User user){
        if(user.getRole().equals("ROLE_OPERATOR")){
            if(! document.getUser().equals(user)){
                throw new CustomException("用户无权限删除他人上传文档", HttpStatus.FORBIDDEN);
            }
        }

        documentRepository.delete(document);
    }




}
