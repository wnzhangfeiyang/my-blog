package com.my.blog.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.my.blog.website.dao.*;
import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.enums.TipExceptionEnums;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.ArchiveBo;
import com.my.blog.website.modal.Vo.*;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.utils.TaleUtils;
import com.my.blog.website.utils.backup.Backup;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.admin.AttachController;
import com.my.blog.website.dto.Types;
import com.my.blog.website.modal.Bo.BackResponseBo;
import com.my.blog.website.modal.Bo.StatisticsBo;
import com.my.blog.website.utils.ZipUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by BlueT on 2017/3/7.
 */
@Service
public class SiteServiceImpl implements ISiteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Resource
    private CommentsMapper commentDao;

    @Resource
    private ContentsMapper contentDao;

    @Resource
    private AttachMapper attachDao;

    @Resource
    private MetasMapper metaDao;

    @Override
    public List<CommentVo> recentComments(Integer userId, int limit) {
        LOGGER.debug("Enter recentComments method:limit={}", limit);
        if(Objects.isNull(userId) || userId == 0){
            throw new TipException(TipExceptionEnums.USER_ID_IS_VALID);
        }
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        PageHelper.startPage(1, limit);
        List<CommentVo> byPage = null;
        try {
            byPage = commentDao.selectList(new QueryWrapper<CommentVo>().lambda().eq(CommentVo::getAuthorId, userId).orderByDesc(CommentVo::getCreated));
        } catch (Exception exception) {
            LOGGER.info("e:{}", exception.getMessage());
        }
        LOGGER.debug("Exit recentComments method");
        return byPage;
    }

    @Override
    public List<ContentVo> recentContents(Integer userId, int limit) {
        LOGGER.debug("Enter recentContents method");
        if(Objects.isNull(userId) || userId == 0){
            throw new TipException(TipExceptionEnums.USER_ID_IS_VALID);
        }
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        PageHelper.startPage(1, limit);
        List<ContentVo> list = contentDao.selectList(new QueryWrapper<ContentVo>().lambda().
                eq(ContentVo::getStatus, Types.PUBLISH.getType()).
                eq(ContentVo::getType, Types.ARTICLE.getType()).
                eq(ContentVo::getAuthorId, userId).orderByDesc(ContentVo::getCreated));
        LOGGER.debug("Exit recentContents method");
        return list;
    }

    @Override
    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponseBo backResponse = new BackResponseBo();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if (!(new File(bk_path)).isDirectory()) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        }
        if (bk_type.equals("db")) {

            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if (!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(TaleUtils.getNewDataSource().getConnection());
            String sqlContent = backup.execute();

            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);

            if (!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();

            backResponse.setSqlPath(zipFile);

            // 10秒后删除备份文件
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }
        return backResponse;
    }

    @Override
    public CommentVo getComment(Integer coid) {
        if (null != coid) {
            return commentDao.selectById(coid);
        }
        return null;
    }

    @Override
    public StatisticsBo getStatistics(Integer userId) {
        LOGGER.debug("Enter getStatistics method");
        StatisticsBo statistics = new StatisticsBo();


        Long articles =  Long.parseLong(String.valueOf(contentDao.selectCount(new QueryWrapper<ContentVo>().lambda().
                eq(ContentVo::getStatus, Types.PUBLISH.getType()).
                eq(ContentVo::getType, Types.ARTICLE.getType()).
                eq(ContentVo::getAuthorId, userId))));


        Long comments = Long.parseLong(String.valueOf(commentDao.selectCount(new QueryWrapper<CommentVo>().lambda().eq(CommentVo::getAuthorId, userId))));


        Long attachs = Long.parseLong(String.valueOf(attachDao.selectCount(new QueryWrapper<AttachVo>().lambda().eq(AttachVo::getAuthorId, userId))));


        Long links = Long.parseLong(String.valueOf(metaDao.selectCount(new QueryWrapper<MetaVo>().lambda().eq(MetaVo::getType, Types.LINK.getType()))));

        statistics.setArticles(articles);
        statistics.setComments(comments);
        statistics.setAttachs(attachs);
        statistics.setLinks(links);
        LOGGER.debug("Exit getStatistics method");
        return statistics;
    }

    @Override
    public List<ArchiveBo> getArchives() {
        LOGGER.debug("Enter getArchives method");
        List<ArchiveBo> archives = contentDao.findReturnArchiveBo();
        if (null != archives) {
            archives.forEach(archive -> {
                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                List<ContentVo> contentss = contentDao.selectList(new QueryWrapper<ContentVo>().lambda().
                        eq(ContentVo::getStatus, Types.PUBLISH.getType()).
                        eq(ContentVo::getType, Types.ARTICLE.getType()).
                        between(ContentVo::getCreated, start, end).orderByDesc(ContentVo::getCreated));
                archive.setArticles(contentss);
            });
        }
        LOGGER.debug("Exit getArchives method");
        return archives;
    }

    @Override
    public List<MetaDto> metas(String type, String orderBy, int limit){
        LOGGER.debug("Enter metas method:type={},order={},limit={}", type, orderBy, limit);
        List<MetaDto> retList=null;
        if (StringUtils.isNotBlank(type)) {
            if(StringUtils.isBlank(orderBy)){
                orderBy = "count desc, a.mid desc";
            }
            if(limit < 1 || limit > WebConst.MAX_POSTS){
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderBy);
            paraMap.put("limit", limit);
            retList= metaDao.selectFromSql(paraMap);
        }
        LOGGER.debug("Exit metas method");
        return retList;
    }


    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch (IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if(null != os) {
                try {
                    os.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

}
