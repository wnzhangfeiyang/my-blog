package com.my.blog.website.controller;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.config.DistributeLock;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dto.CommentDTO;
import com.my.blog.website.dto.ErrorCode;
import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.ArchiveBo;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.*;
import com.my.blog.website.service.*;
import com.my.blog.website.utils.PatternKit;
import com.my.blog.website.utils.TaleUtils;
import com.vdurmont.emoji.EmojiParser;
import com.my.blog.website.modal.Bo.CommentBo;
import com.my.blog.website.utils.IPKit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * 首页
 * Created by Administrator on 2017/3/8 008.
 */
@Controller
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Resource
    private IContentService contentService;

    @Resource
    private ICommentService commentService;

    @Resource
    private IMetaService metaService;

    @Resource
    private ISiteService siteService;

    @Resource
    private DistributeLock distributeLock;

    @Resource
    private IUserService userService;

    /**
     * 首页
     *
     * @return
     */
    @GetMapping(value = {"/", "index"})
    public String index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.index(request, 1, limit);
    }

    /**
     * 首页分页
     *
     * @param request request
     * @param p       第几页
     * @param limit   每页大小
     * @return 主页
     */
    @GetMapping(value = "page/{p}")
    public String index(HttpServletRequest request, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 0 || p > WebConst.MAX_PAGE ? 1 : p;
        PageInfo<ContentVo> articles = contentService.getContents(p, limit);
        request.setAttribute("articles", articles);
        if (p > 1) {
            this.title(request, "第" + p + "页");
        }
        return this.render("index");
    }

    /**
     * 文章页
     *
     * @param request 请求
     * @param cid     文章主键
     * @return
     */
    @GetMapping(value = {"article/{cid}", "article/{cid}.html"})
    public String getArticle(HttpServletRequest request, @PathVariable String cid) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents || "draft".equals(contents.getStatus())) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("post");
    }

    /**
     * 文章页(预览)
     *
     * @param request 请求
     * @param cid     文章主键
     * @return
     */
    @GetMapping(value = {"article/{cid}/preview", "article/{cid}.html"})
    public String articlePreview(HttpServletRequest request, @PathVariable String cid) {
        ContentVo contents = contentService.getContents(cid);
        if (null == contents) {
            return this.render_404();
        }
        request.setAttribute("article", contents);
        request.setAttribute("is_post", true);
        completeArticle(request, contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("post");
    }

    /**
     * 抽取公共方法
     *
     * @param request
     * @param contents
     */
    private void completeArticle(HttpServletRequest request, ContentVo contents) {
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
    }

    /**
     * 注销
     *
     * @param session
     * @param response
     */
    @RequestMapping("logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session, response);
    }

    /**
     * 评论操作
     */
    @PostMapping(value = "comment")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response, @RequestBody CommentDTO commentDTO) {
        UserVo userInfo = userService.getUserInfo(request);
        String ref = request.getHeader("Referer");
        String _csrf_token = commentDTO.get_csrf_token();
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
            throw new TipException(ErrorCode.BAD_REQUEST);
        }
        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
        if (StringUtils.isBlank(token)) {
            throw new TipException(ErrorCode.BAD_REQUEST);
        }
        // 请求路径
        try {
            // 设置对每个文章进行分布式锁，1分钟可评论一次
            // 设置新的分布式锁，这种锁有点老， 采用stringRedisTemplate进行分布式锁
            Boolean locked = distributeLock.isLocked(String.valueOf(commentDTO.getCid() + userInfo.getUid()));
            if(locked){
                throw new TipException("评论过快，休息一下再来哦～～");
            }
            commentService.comment(request, response, commentDTO, userInfo);
//            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);

            return RestResponseBo.ok();
        } catch (Exception e) {
            String msg = "评论发布失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.info("评论发布失败:{}", e.getMessage());

            }
            return RestResponseBo.fail(msg);
        }
    }


    /**
     * 分类页
     *
     * @return
     */
    @GetMapping(value = "category/{keyword}")
    public String categories(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.categories(request, keyword, 1, limit);
    }

    @GetMapping(value = "category/{keyword}/{page}")
    public String categories(HttpServletRequest request, @PathVariable String keyword,
                             @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        MetaDto metaDto = metaService.getMeta(Types.CATEGORY.getType(), keyword);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);

        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", keyword);

        return this.render("page-category");
    }


    /**
     * 归档页
     *
     * @return
     */
    @GetMapping(value = "archives")
    public String archives(HttpServletRequest request) {
        List<ArchiveBo> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return this.render("archives");
    }

    /**
     * 友链页
     *
     * @return
     */
    @GetMapping(value = "links")
    public String links(HttpServletRequest request) {
        List<MetaVo> links = metaService.getMetas(Types.LINK.getType());
        request.setAttribute("links", links);
        return this.render("links");
    }

    /**
     * 自定义页面,如关于的页面
     */
    @GetMapping(value = "/{pagename}")
    public String page(@PathVariable String pagename, HttpServletRequest request) {
        ContentVo contents = contentService.getContents(pagename);
        if (null == contents) {
            return this.render_404();
        }
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
        request.setAttribute("article", contents);
        updateArticleHit(contents.getCid(), contents.getHits());
        return this.render("page");
    }


    /**
     * 搜索页
     *
     * @param keyword
     * @return
     */
    @GetMapping(value = "search/{keyword}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.search(request, keyword, 1, limit);
    }

    @GetMapping(value = "search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
        PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
        request.setAttribute("articles", articles);
        request.setAttribute("type", "搜索");
        request.setAttribute("keyword", keyword);
        return this.render("page-category");
    }

    /**
     * 更新文章的点击率
     *
     * @param cid
     * @param chits
     */
    @Transactional(rollbackFor = TipException.class)
    private void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article", "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article", "hits", 1);
        } else {
            cache.hset("article", "hits", hits);
        }
    }

    /**
     * 标签页
     *
     * @param name
     * @return
     */
    @GetMapping(value = "tag/{name}")
    public String tags(HttpServletRequest request, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, name, 1, limit);
    }

    /**
     * 标签分页
     *
     * @param request
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "tag/{name}/{page}")
    public String tags(HttpServletRequest request, @PathVariable String name, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
//        对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        MetaDto metaDto = metaService.getMeta(Types.TAG.getType(), name);
        if (null == metaDto) {
            return this.render_404();
        }

        PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);
        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", name);

        return this.render("page-category");
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

}
