package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.dto.CommentDTO;
import com.my.blog.website.modal.Vo.CommentVo;
import com.my.blog.website.modal.Bo.CommentBo;
import com.my.blog.website.modal.Vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by BlueT on 2017/3/16.
 */
public interface ICommentService {

    /**
     * 保存对象
     * @param commentVo
     */
    void insertComment(CommentVo commentVo);

    /**
     * 获取文章下的评论
     * @param cid
     * @param page
     * @param limit
     * @return CommentBo
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);

    /**
     * 获取文章下的评论
     *
     * @param uid
     * @param page
     * @param limit
     * @return CommentVo
     */
    PageInfo<CommentVo> getCommentsWithPage(Integer uid, int page, int limit);


    /**
     * 根据主键查询评论
     * @param coid
     * @return
     */
    CommentVo getCommentById(Integer coid);


    /**
     * 删除评论，暂时没用
     * @param coid
     * @param cid
     * @throws Exception
     */
    void delete(Integer coid, Integer cid);

    /**
     * 更新评论状态
     * @param comments
     */
    void update(CommentVo comments);

    void comment(HttpServletRequest request, HttpServletResponse response, CommentDTO commentDTO, UserVo userInfo);
}
