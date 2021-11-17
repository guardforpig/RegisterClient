package cn.edu.xmu.oomall.comment.dao;

import cn.edu.xmu.oomall.comment.mapper.CommentPoMapper;
import cn.edu.xmu.oomall.comment.model.bo.Comment;
import cn.edu.xmu.oomall.comment.model.po.CommentPo;
import cn.edu.xmu.oomall.comment.model.po.CommentPoExample;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;

import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentDao {
    @Autowired
    CommentPoMapper commentPoMapper;

//    @Autowired
//    SKUPoMapper skuPoMapper;
    /**
     * 获取评论所有状态
     * @param[]
     * @return CommentPo
     */
    public ReturnObject getCommentStates(){
        List<Map<String,Object>> stateList=new ArrayList<>();
        for(Comment.State state:Comment.State.values()){
            Map<String,Object> temp=new HashMap<>();
            temp.put("code",state.getCode());
            temp.put("name",state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }


    /**
     * 买家新增一条评论
     * @param commentPo
     * @return
     */
    public ReturnObject insertComment(CommentPo commentPo){
        try{
            commentPo.setGmtCreate(LocalDateTime.now());
            commentPo.setGmtModified(commentPo.getGmtModified());
            int ret=commentPoMapper.insertSelective(commentPo);
            if(ret==0){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject(commentPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 判断某个订单明细是否已被评论
     */
    public ReturnObject judgeComment(Long orderItemId){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        criteria.andOrderitemIdEqualTo(orderItemId);
        List<CommentPo> commentPos;
        try {
           commentPos = commentPoMapper.selectByExample(example);
        }
        catch (Exception e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        for(CommentPo commentPo:commentPos){
            if(commentPo.getState()==Comment.State.NOT_AUDIT.getCode()||commentPo.getState()==Comment.State.PASS.getCode()){
                return new ReturnObject<>(false);
            }
        }
        return new ReturnObject<>(true);
    }


    /**
     * 分页查询product下所有已通过审核的评论
     *
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return ReturnObject<>
     */
    public ReturnObject selectAllPassCommentByProductId(Long productId,Integer pageNum,Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andProductIdEqualTo(productId);
            criteria.andStateEqualTo(Comment.State.PASS.getCode());
            commentPos=commentPoMapper.selectByExample(example);

        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(commentPos);
    }
    /**
     * 分页查询店铺评论
     * @return ReturnObject<>
     */
    public ReturnObject selectCommentByShopId(Long shopId,Integer pageNum,Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
//            criteria.andShopIdEqualTo(shopId);
            commentPos=commentPoMapper.selectByExample(example);

        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(commentPos);
    }

    /**
     * 修改评论状态
     */
    public ReturnObject updateCommentState(Comment comment){
//        CommentPo commentPo=comment.createPo();
        CommentPo commentPo = (CommentPo) Common.cloneVo(comment, CommentPo.class);
        int ret;
        try{
            ret=commentPoMapper.updateByPrimaryKeySelective(commentPo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if (ret == 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {
            return new ReturnObject();
        }
    }


    /**
     * 买家查看自己的评价记录，包括评论状态
     */
    public ReturnObject selectAllCommentsOfUser(Long userId,Integer pageNum, Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        criteria.andCreatedByEqualTo(userId);
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andProductIdEqualTo(userId);
            criteria.andStateEqualTo(Comment.State.PASS.getCode());
            commentPos=commentPoMapper.selectByExample(example);

        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(commentPos);

    }

    /**
     *管理员查看未审核评论列表
     */
    public ReturnObject selelctCommentsOfState(Byte state,Integer pageNum, Integer pageSize){
        CommentPoExample example=new CommentPoExample();
        CommentPoExample.Criteria criteria=example.createCriteria();
        List<CommentPo> commentPos=new ArrayList<>();
        PageHelper.startPage(pageNum,pageSize);
        try{
            criteria.andStateEqualTo(state);
            commentPos=commentPoMapper.selectByExample(example);

        }catch (DataAccessException e){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return new ReturnObject<>(commentPos);
    }
}


