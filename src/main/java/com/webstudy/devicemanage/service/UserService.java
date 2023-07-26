package com.webstudy.devicemanage.service;

import com.webstudy.devicemanage.dto.UserDataDTO;
import com.webstudy.devicemanage.dto.UserResponseDTO;
import com.webstudy.devicemanage.exception.CustomException;
import com.webstudy.devicemanage.model.LogEntity;
import com.webstudy.devicemanage.model.PasswordRecord;
import com.webstudy.devicemanage.model.User;
//import com.webstudy.devicemanage.repository.PasswordRepository;
import com.webstudy.devicemanage.repository.UserRepository;
import com.webstudy.devicemanage.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webstudy.devicemanage.utils.PwdCheckUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;



    public Map<String, String> login(String username, String password, String uuid, String captcha) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String jwtToken = jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRole());

            // 验证验证码
            String storedCaptcha = redisTemplate.opsForValue().get(uuid);

            // TODO:上线前加入验证码使用一次后自动过期
//            redisTemplate.delete(uuid);

            if (storedCaptcha != null && storedCaptcha.equalsIgnoreCase(captcha)) {
                String roleCode = userRepository.findByUsername(username).getRole();
                return new HashMap<String, String>(){{
                    put("token", jwtToken);
                    put("role", roleCode);
                }};
            }
            else{
                throw new CustomException("验证码不正确", HttpStatus.FORBIDDEN);
            }


        } catch (AuthenticationException e) {
            throw new CustomException("用户名或密码错误", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            String password = user.getPassword();

            if(PwdCheckUtil.checkByCustomRules(password)){
                user.setPassword(passwordEncoder.encode(password));
            }
            else{
                throw new CustomException("密码不符合要求，为了您的安全请检查！", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } else {
            throw new CustomException("用户名已占用", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!(user.getRoleList().contains(user.getRole()))){
            throw new CustomException("角色码无效", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.save(user);
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole());
    }

    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public User search(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("用户不存在", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User myself(HttpServletRequest req) {
        return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username) {
        return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRole());
    }

    public User modifyPermissions(UserResponseDTO user) {
        User tmpUser = userRepository.findByUsername(user.getUsername());
        tmpUser.setRole(user.getRole());
        userRepository.save(tmpUser);
        return tmpUser;
    }

    public User adminModifyPassword(UserDataDTO user){
        User tmpUser = userRepository.findByUsername(user.getUsername());

        String password = user.getPassword();
        if(PwdCheckUtil.checkByCustomRules(password)){
            tmpUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        else{
            throw new CustomException("密码不符合要求，为了您的安全请检查！", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.save(tmpUser);
        return tmpUser;
    }

    public User ModifyPassword(HttpServletRequest req, UserDataDTO user){
        User tmpUser = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
        if(! user.getUsername().equals(tmpUser.getUsername())){
            throw new CustomException("非法访问", HttpStatus.FORBIDDEN);
        }

        String password = user.getPassword();
        if(PwdCheckUtil.checkByCustomRules(password)){
            tmpUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        else{
            throw new CustomException("密码不符合要求，为了您的安全请检查！", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.save(tmpUser);
        return tmpUser;
    }

    public void logout(HttpServletRequest req){

        String username = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
        jwtTokenProvider.deleteToken(username);
    }

    public boolean forceLogout(String userName){
        try {
            jwtTokenProvider.deleteToken(userName);
            return true;
        }catch (CustomException exception){
            return false;
        }

    }

    public List<User> getAll() {
        return userRepository.findAll();
    }


}
