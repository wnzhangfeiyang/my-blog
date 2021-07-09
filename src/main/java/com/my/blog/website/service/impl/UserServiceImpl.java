package com.my.blog.website.service.impl;

import com.google.common.collect.Lists;
import com.my.blog.website.dao.UserVoMapper;
import com.my.blog.website.enums.TipExceptionEnums;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.TaleUtils;
import com.my.blog.website.modal.Vo.UserVoExample;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private UserVoMapper userDao;

    @Override
    public Integer insertUser(UserVo userVo) {
        if (StringUtils.isNotBlank(userVo.getUsername()) && StringUtils.isNotBlank(userVo.getEmail())) {
//            用户密码加密
            String encodePwd = TaleUtils.MD5encode(userVo.getPassword());
            userVo.setPassword(encodePwd);
             userDao.insertSelective(userVo);
        }
        return userVo.getUid();
    }

    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if (uid != null) {
            userVo = userDao.selectByPrimaryKey(uid);
        }
        return userVo;
    }

    @Override
    public UserVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        UserVoExample example = new UserVoExample();
        UserVoExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = userDao.countByExample(example);
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(password);
        criteria.andPasswordEqualTo(pwd);
        List<UserVo> userVos = userDao.selectByExample(example);
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
        int i = userDao.updateByPrimaryKeySelective(userVo);
        if(i!=1){
            throw new TipException("update user by uid and retrun is not one");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(String username, String password, String comfirmPassword, String email) {
        checkFildes(username, password, comfirmPassword, email);
        Long userVos = 0L;
        UserVo userVo = new UserVo();
        UserVoExample userVoExample = new UserVoExample();
        userVoExample.createCriteria().andUsernameEqualTo(username);
        userVos = userDao.countByExample(userVoExample);
        // 用户名是唯一的
        if(userVos > 0L){
            throw new TipException(TipExceptionEnums.USERNAME_IS_JUST_ONE);
        }
        // 邮箱也是唯一的
        userVoExample.clear();
        userVoExample.createCriteria().andEmailEqualTo(email);
        userVos = userDao.countByExample(userVoExample);
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
}
