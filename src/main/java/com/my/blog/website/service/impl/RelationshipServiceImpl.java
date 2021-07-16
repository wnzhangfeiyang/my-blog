package com.my.blog.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.my.blog.website.dao.RelationshipsMapper;
import com.my.blog.website.modal.Vo.RelationshipVoKey;
import com.my.blog.website.service.IRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BlueT on 2017/3/18.
 */
@Service
public class RelationshipServiceImpl implements IRelationshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipServiceImpl.class);

    @Resource
    private RelationshipsMapper shipMapper;

    @Override
    public void deleteById(Integer cid, Integer mid) {
        shipMapper.delete(new QueryWrapper<RelationshipVoKey>().lambda().eq(StringUtils.isNotEmpty(String.valueOf(cid)), RelationshipVoKey::getCid, cid).eq(StringUtils.isNotEmpty(String.valueOf(mid)), RelationshipVoKey::getMid, mid));
    }

    @Override
    public List<RelationshipVoKey> getRelationshipById(Integer cid, Integer mid) {
        return shipMapper.selectList(new QueryWrapper<RelationshipVoKey>().lambda().eq(StringUtils.isNotEmpty(String.valueOf(cid)), RelationshipVoKey::getCid, cid).eq(StringUtils.isNotEmpty(String.valueOf(mid)), RelationshipVoKey::getMid, mid));
    }

    @Override
    public void insertVo(RelationshipVoKey relationshipVoKey) {
        shipMapper.insert(relationshipVoKey);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}",cid,mid);
        Integer num = shipMapper.selectCount(new QueryWrapper<RelationshipVoKey>().lambda().eq(StringUtils.isNotEmpty(String.valueOf(cid)), RelationshipVoKey::getCid, cid).eq(StringUtils.isNotEmpty(String.valueOf(mid)), RelationshipVoKey::getMid, mid));
        LOGGER.debug("Exit countById method return num={}",num);
        return Long.parseLong(String.valueOf(num));
    }
}
