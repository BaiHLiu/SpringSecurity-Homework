package com.webstudy.devicemanage.controller;

import com.webstudy.devicemanage.annotation.Log;
import com.webstudy.devicemanage.dto.CustomResultDTO;
import com.webstudy.devicemanage.dto.TokenResponseDTO;
import com.webstudy.devicemanage.dto.UserDataDTO;
import com.webstudy.devicemanage.dto.UserResponseDTO;
import com.webstudy.devicemanage.exception.CustomException;
import com.webstudy.devicemanage.model.LogEntity;
import com.webstudy.devicemanage.model.User;
import com.webstudy.devicemanage.service.LogService;
import com.webstudy.devicemanage.service.UserService;
import com.webstudy.devicemanage.utils.CaptchaUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LogService logService;


    @Log("/用户/用户登陆")
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDataDTO user, HttpServletRequest request) {
        if(request.getParameter("uuid") == null || request.getParameter("captcha") == null){
            throw new CustomException("参数不正确", HttpStatus.FORBIDDEN);
        }

        return userService.login(user.getUsername(), user.getPassword(), request.getParameter("uuid"), request.getParameter("captcha"));
    }

    @Log("/用户/用户注册")
    @PostMapping("/signup")
    public TokenResponseDTO signup(@RequestBody UserDataDTO user) {
        user.setRole("ROLE_WORKER");
        return new TokenResponseDTO(userService.signup(modelMapper.map(user, User.class)));
    }

    @Log("/用户/管理员/添加用户")
    @PostMapping("/admin/add")
    public TokenResponseDTO adminAddUser(@RequestBody UserDataDTO user) {
        return new TokenResponseDTO(userService.signup(modelMapper.map(user, User.class)));
    }

    @Log("/用户/管理员/根据用户名搜索用户")
    @GetMapping("/admin/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDTO search(@PathVariable String username) {
        return modelMapper.map(userService.search(username), UserResponseDTO.class);
    }

    @Log("/用户/管理员/根据用户名删除用户")
    @DeleteMapping("/admin/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CustomResultDTO delete(@PathVariable String username) {
        userService.delete(username);
        return new CustomResultDTO("success");
    }


    @Log("/用户/获取当前用户")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO me(HttpServletRequest request) {
        return modelMapper.map(userService.myself(request), UserResponseDTO.class);
    }

//    @GetMapping("/refresh")
//    @PreAuthorize("isAuthenticated()")
//    public TokenResponseDTO refresh(HttpServletRequest request) {
//        return new TokenResponseDTO(userService.refresh(request.getRemoteUser()));
//    }

    @Log("/用户/管理员/修改用户权限")
    @PatchMapping("/admin/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDTO modifyPermissions(@RequestBody UserResponseDTO user) {
        return modelMapper.map(userService.modifyPermissions(user), UserResponseDTO.class);
    }

    @Log("/用户/管理员/列出所有用户")
    @GetMapping("/admin/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return modelMapper.map(userService.getAll(), new TypeToken<List<UserResponseDTO>>() {
        }.getType());
    }

    @Log("/用户/管理员/修改用户密码")
    @PatchMapping("/admin/password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDTO adminModifyPassword(@RequestBody UserDataDTO user){
        return modelMapper.map(userService.adminModifyPassword(user), UserResponseDTO.class);
    }

    @Log("/用户/修改用户密码")
    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO modifyPassword(HttpServletRequest request, @RequestBody UserDataDTO user){
        return modelMapper.map(userService.ModifyPassword(request, user), UserResponseDTO.class);
    }



    @Log("/用户/用户登出")
    @DeleteMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> userLogout(HttpServletRequest request){
        userService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @Log("/用户/管理员/强制下线用户")
    @DeleteMapping("/admin/forceLogout")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> forceLogout(String userName){
        userService.forceLogout(userName);
        return ResponseEntity.noContent().build();
    }

    @Log("/用户/管理员/查看日志")
    @GetMapping("/admin/log/query")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<LogEntity> queryLog(){
        return logService.getAll();
    }


    @GetMapping("/login/captcha")
    public Map<String, Object> getCaptchaImage() throws Exception {
        //生成随机验证码字符串和UUID
        String captcha = CaptchaUtil.generateCaptcha();
        String uuid = UUID.randomUUID().toString();
        //将验证码字符串和UUID存储到Redis中
        redisTemplate.opsForValue().set(uuid, captcha, 300, TimeUnit.SECONDS);
        //生成验证码图片，并将图片转换为base64编码格式
        BufferedImage image = CaptchaUtil.generateCaptchaImage(captcha);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        String captchaBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        //构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("captcha", captchaBase64);
        return result;
    }
}