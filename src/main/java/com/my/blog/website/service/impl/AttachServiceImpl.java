package com.my.blog.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.AttachMapper;
import com.my.blog.website.dao.AttachVoMapper;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.modal.Vo.AttachVo;
import com.my.blog.website.modal.Vo.AttachVoExample;
import com.my.blog.website.service.IAttachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangq on 2017/3/20.
 */
@Service
public class AttachServiceImpl implements IAttachService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachServiceImpl.class);

    @Resource
    private AttachMapper attachDao;

    @Override
    public PageInfo<AttachVo> getAttachs(Integer page, Integer limit, Integer uid) {
        PageHelper.startPage(page, limit);
        List<AttachVo> attachVos = attachDao.selectList(new QueryWrapper<AttachVo>().lambda().eq(AttachVo::getAuthorId, uid).orderByDesc(AttachVo::getId));
        return new PageInfo<>(attachVos);
    }

    @Override
    public AttachVo selectById(Integer id) {
        if(null != id){
            return attachDao.selectById(id);
        }
        return null;
    }

    @Override
    public void save(String fname, String fkey, String ftype, Integer author) {
        AttachVo attach = new AttachVo();
        attach.setFname(fname);
        attach.setAuthorId(author);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attach.setCreated(DateKit.getCurrentUnixTime());
        attachDao.insert(attach);
    }

    @Override
    public void deleteById(Integer id) {
        if (null != id) {
            attachDao.deleteById( id);
        }
    }
}
