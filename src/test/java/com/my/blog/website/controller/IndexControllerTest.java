package com.my.blog.website.controller;

import com.my.blog.website.dao.AttachMapper;
import com.my.blog.website.dao.ContentsMapper;
import com.my.blog.website.modal.Vo.AttachVo;
import com.my.blog.website.modal.Vo.ContentVo;
import com.my.blog.website.service.IAttachService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 接口测试方法
 * Created by BlueT on 2017/3/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Resource
    private AttachMapper attachMapper;

    @Resource
    private ContentsMapper contentsMapper;

    @Test
    @Ignore
    public void index() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("")).andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void selectAttach(){
        Integer id = 1;
        ContentVo attachVo = contentsMapper.selectById(id);
        System.out.println(attachVo);
    }
}