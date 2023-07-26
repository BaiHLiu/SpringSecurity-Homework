package com.webstudy.devicemanage.controller;

import com.webstudy.devicemanage.annotation.Log;
import com.webstudy.devicemanage.dto.*;
import com.webstudy.devicemanage.model.*;
import com.webstudy.devicemanage.repository.DocumentRepository;
import com.webstudy.devicemanage.security.JwtTokenProvider;
import com.webstudy.devicemanage.service.DeviceService;
import com.webstudy.devicemanage.service.DocumentService;
import com.webstudy.devicemanage.service.UserService;
import com.webstudy.devicemanage.utils.RedisConfig;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    private ModelMapper modelMapper = new ModelMapper();
    private final JwtTokenProvider jwtTokenProvider;
    public DeviceController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Value("${fileUpload.uploadPath}")
    private String uploadPath;


    @Log("/设备/获取全部设备")
    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public List<Device> getAllDevices() {
        return deviceService.getAllDevice();
    }

    @Log("/设备/添加设备")
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Device addDevice(@RequestBody DeviceAddDTO device) {
        return deviceService.addDevice(device);
    }

    @Log("/设备/根据ID获取设备详情")
    @GetMapping("/{device}")
    @PreAuthorize("isAuthenticated()")
    public Device getDevice(@PathVariable Integer device) {
        return deviceService.getDevice(device);
    }

    @Log("/设备/修改设备")
    @PostMapping("/device")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Device modifyDevide(@RequestBody Device device) {
        return modelMapper.map(deviceService.modifyDevice(device), Device.class);
    }

    @Log("/设备/删除设备")
    @DeleteMapping("/{deviceId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OPERATOR')")
    public ResponseEntity<Void> deleteDevice(@PathVariable Integer deviceId){
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.noContent().build();
    }


    @Log("/设备/维修/添加维修")
    @PostMapping("/{device}/repair")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_WORKER')")
    public Repair addRepair(@PathVariable Integer device, @RequestBody RepairDTO repair) {
        return deviceService.addRepair(device, repair);
    }

    @Log("/设备/维修/完成维修")
    @PostMapping("/repair/{repair}/finish")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR')")
    public Repair finishRepair(@PathVariable Integer repair, @RequestBody FinishedDTO data, HttpServletRequest req) {

        String execName = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
        return deviceService.finishRepair(repair, data, execName);
    }

    @Log("/设备/维修/获取全部维修")
    @GetMapping("/repair")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public List<Repair> getAllRepairs() {
        return deviceService.getAllRepair();
    }

    @Log("/设备/保养/添加保养")
    @PostMapping("/{device}/maintain")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_WORKER')")
    public Maintain addMaintain(@PathVariable Integer device, @RequestBody MaintainDTO maintain) {
        return deviceService.addMaintain(device, maintain);
    }

    @Log("/设备/保养/完成保养")
    @PostMapping("/maintain/{maintain}/finish")
    @PreAuthorize("hasRole('ROLE_WORKER')")
    public Maintain finishMaintain(@PathVariable Integer maintain, @RequestBody FinishedDTO data) {
        return deviceService.finishMaintain(maintain, data);
    }

    @Log("/设备/文档/添加文档")
    @PostMapping("/{device}/document")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public Document addDocument(@PathVariable Integer device, @RequestBody DocumentDTO document, HttpServletRequest request) {
        User uploadUser = userService.myself(request);
        return deviceService.addDocument(device, document, uploadUser);

    }

    @Log("/设备/文档/删除文档")
    @DeleteMapping("/document/{document}/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer document, HttpServletRequest request) {
        User user = userService.myself(request);
        Document tmpDoc = deviceService.getDocumentDetails(document);

        deviceService.deleteDocument(tmpDoc, user);
        return ResponseEntity.noContent().build();
    }

    @Log("/设备/下载文件")
    @GetMapping("/document/downloadFile/{documentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_WORKER')")
    public DocumentDTO downloadFile(@PathVariable Integer documentId){
        DocumentDTO documentDTO =  modelMapper.map(deviceService.getDocumentDetails(documentId), DocumentDTO.class);
        documentDTO.setUuid(documentService.getUuid(documentId));
        return documentDTO;
    }


    @Log("/设备/上传文件")
    @PostMapping("/document/uploadFile")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // 生成uuid
        String uuid = UUID.randomUUID().toString();
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 上传文件
        File dest = new File(uploadPath + uuid);
        file.transferTo(dest);
        return uuid;
    }



}
