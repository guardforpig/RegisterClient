package cn.edu.xmu.oomall.comment.service;


import cn.edu.xmu.oomall.comment.dao.CommentDao;
import cn.edu.xmu.oomall.comment.model.bo.Comment;
import cn.edu.xmu.oomall.comment.model.po.CommentPo;
import cn.edu.xmu.oomall.comment.model.vo.*;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 * @date:2021/11/21
 **/
@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;


    /**
     * 获取评论所有状态
     *
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getCommentStates() {
        return commentDao.getCommentStates();
    }

    /**
     * 买家新增评论
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject newComment(Long productId, CommentVo commentVo, Long loginUser, String loginUsername) {

        Long shopId = commentVo.getShopId();
        CommentPo commentPo = new CommentPo();

        commentPo.setProductId(productId);
        commentPo.setContent(commentVo.getContent());
        commentPo.setShopId(shopId);
        commentPo.setType(commentVo.getType().byteValue());
        commentPo.setState(Comment.State.NOT_AUDIT.getCode());
        Common.setPoCreatedFields(commentPo, loginUser, loginUsername);
        ReturnObject ret_insert = commentDao.insertComment(commentPo);
        if (ret_insert.getCode().equals(0)) {
            CommentRetVo commentRetVo = (CommentRetVo) Common.cloneVo(commentPo, CommentRetVo.class);
            return new ReturnObject(commentRetVo);
        }
        return ret_insert;
    }

    /**
     * 分页查询商品下所有已通过审核的评论
     *
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectAllPassCommentByProductId(Long productId, Integer pageNum, Integer pageSize) {
        List<CommentPo> commentPos = (List<CommentPo>) commentDao.selectAllPassCommentByProductId(productId, pageNum, pageSize).getData();
        List<Object> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            commentRetVos.add(po);
        }
        PageInfo<Object> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }

    /**
     * 查询商铺评论
     *
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectAllPassCommentByShopId(Long shopId, Integer pageNum, Integer pageSize) {

        List<CommentPo> commentPos = (List<CommentPo>) commentDao.selectCommentByShopId(shopId, pageNum, pageSize).getData();
        List<Object> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            commentRetVos.add(po);
        }
        PageInfo<Object> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }

    /**
     * 管理员审核评论
     *
     * @param id
     * @param conclusion
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmCommnets(Long did, Long id, CommentConclusionVo conclusion, Long loginUser, String loginUserName) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setState(conclusion.getConclusion() == true ? Comment.State.PASS.getCode() : Comment.State.FORBID.getCode());
        Common.setPoModifiedFields(comment, loginUser, loginUserName);
        ReturnObject ret = commentDao.updateCommentState(comment);
        return ret;
    }

    /**
     * 买家查看自己的评价记录，包括评论状态
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectAllCommentsOfUser(Long userId, Integer pageNum, Integer pageSize) {
        List<CommentPo> commentPos = (List<CommentPo>) commentDao.selectAllCommentsOfUser(userId, pageNum, pageSize).getData();
        List<Object> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            commentRetVos.add(po);
        }
        //分页查询
        PageInfo<Object> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }

    /**
     * 管理员查看未审核评论列表
     *
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<PageInfo<Object>> selectCommentsOfState(Long did, Integer state, Integer pageNum, Integer pageSize) {

        List<CommentPo> commentPos = (List<CommentPo>) commentDao.selelctCommentsOfState(state.byteValue(), pageNum, pageSize).getData();

        List<Object> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            commentRetVos.add(po);
        }

        PageInfo<Object> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }


}

