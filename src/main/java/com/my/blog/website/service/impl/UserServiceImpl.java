package com.my.blog.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.my.blog.website.dao.UsersMapper;
import com.my.blog.website.enums.TipExceptionEnums;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BlueT on 2017/3/3.
 */
@Service
public class UserServiceImpl implements IUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String emailExp = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    @Resource
    private UsersMapper userDao;

    @Override
    public Integer insertUser(UserVo userVo) {
        if (StringUtils.isNotBlank(userVo.getUsername()) && StringUtils.isNotBlank(userVo.getEmail())) {
//            用户密码加密
            String encodePwd = TaleUtils.MD5encode(userVo.getPassword());
            userVo.setPassword(encodePwd);
             userDao.insert(userVo);
        }
        return userVo.getUid();
    }

    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if (uid != null) {
            userVo = userDao.selectById(uid);
        }
        return userVo;
    }

    @Override
    public UserVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }

        long count = userDao.selectCount(new QueryWrapper<UserVo>().lambda().eq(UserVo::getUsername, username));
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(password);
        List<UserVo> userVos = userDao.selectList(new QueryWrapper<UserVo>().lambda().eq(UserVo::getUsername, username).eq(UserVo::getPassword, pwd));
        if (userVos.size()!=1) {
            throw new TipException("用户名或密码错误");
        }
        return userVos.get(0);
    }

    @Override
    public void updateByUid(UserVo userVo) {
        if (null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = userDao.updateById(userVo);
        if(i!=1){
            throw new TipException("update user by uid and retrun is not one");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(String username, String password, String comfirmPassword, String email) {
        checkFildes(username, password, comfirmPassword, email);
        Integer userVos = 0;
        UserVo userVo = new UserVo();
        userVos = userDao.selectCount(new QueryWrapper<UserVo>().lambda().eq(UserVo::getUsername, username));
        // 用户名是唯一的
        if(userVos > 0){
            throw new TipException(TipExceptionEnums.USERNAME_IS_JUST_ONE);
        }
        // 邮箱也是唯一的
        userVos = userDao.selectCount(new QueryWrapper<UserVo>().lambda().eq(UserVo::getEmail, email));
        if(userVos > 0L){
            throw new TipException(TipExceptionEnums.EMAIL_IS_JUST_ONE);
        }
        userVo.setUsername(username);
        userVo.setPassword(password);
        userVo.setCreated(Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000)));
        userVo.setEmail(email);
        Integer integer = this.insertUser(userVo);
        if(integer > 0){
            LOGGER.info("用户注册成功id:{}", integer);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void checkFildes(String username, String password, String comfirmPassword, String email) {
        if(StringUtils.isBlank(username)){
            throw new TipException(TipExceptionEnums.USERNAME_IS_NOT_NULL);
        }
        if(StringUtils.isBlank(password) || password.length() < 6 || password.length() > 10){
            throw new TipException(TipExceptionEnums.PASSWORD_IS_VALID);
        }
        if(!Objects.equals(password, comfirmPassword)){
            throw new TipException(TipExceptionEnums.TWICE_PASSWORD_IS_NOT_SAME);
        }
        Pattern compile = Pattern.compile(emailExp);
        Matcher matcher = compile.matcher(email);
        boolean matches = matcher.matches();
        if(!matches){
            throw new TipException(TipExceptionEnums.EMAIL_IS_VALID);
        }
    }

    @Override
    public UserVo getUserInfo(HttpServletRequest request) {
        Integer userId = TaleUtils.getCookieUid(request);
        UserVo userVo = this.queryUserById(userId);
        if(Objects.isNull(userVo)){
            throw new TipException(TipExceptionEnums.USER_ID_IS_VALID);
        }
        return userVo;
    }
}
