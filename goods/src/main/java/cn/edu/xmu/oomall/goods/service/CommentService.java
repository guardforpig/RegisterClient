package cn.edu.xmu.oomall.goods.service;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.dao.CommentDao;
import cn.edu.xmu.oomall.goods.microservice.CustomerService;
import cn.edu.xmu.oomall.goods.microservice.OrderService;
import cn.edu.xmu.oomall.goods.model.bo.Comment;
import cn.edu.xmu.oomall.goods.model.po.CommentPo;
import cn.edu.xmu.oomall.goods.model.vo.CommentConclusionVo;
import cn.edu.xmu.oomall.goods.model.vo.CommentRetVo;
import cn.edu.xmu.oomall.goods.model.vo.CommentVo;
import com.alibaba.nacos.api.common.ResponseCode;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    //    @DubboReference(version = "0.0.1-SNAPSHOT",check = false)
//    private ICustomerService customerService;
//
//    @DubboReference(version = "0.0.1-SNAPSHOT",check = false)
//    private IDubboOrderService orderService;
//
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    /**
     * 获取评论所有状态
     *
     * @return
     */
    public ReturnObject getCommentStates() {
        return commentDao.getCommentStates();
    }

    /**
     * 买家新增评论
     *
     * @param orderItemId
     * @param commentVo
     * @param userId
     * @return
     */
    public ReturnObject newComment(Long orderItemId, CommentVo commentVo, Long loginUser,String loginUsername) {
        ReturnObject ret = orderService.isCustomerOwnOrderItem(loginUser, orderItemId);
        if (!ret.getCode().equals(0)) {
            return ret;
        }
        Boolean result = (Boolean) ret.getData();
        if (!result) {
            //用户没有购买此商品
            return new ReturnObject(ReturnNo.COMMENT_USER_NOORDER);
        }

        if (!commentDao.judgeComment(orderItemId)) {
            return new ReturnObject(ReturnNo.COMMENT_EXISTED, "该订单条目已评论");
        }
        CommentPo commentPo = new CommentPo();
        commentPo.setOrderitemId(orderItemId);
        commentPo.setContent(commentVo.getContent());
        commentPo.setType(commentVo.getType().byteValue());
        commentPo.setState(Comment.State.NOT_AUDIT.getCode());
        commentPo.setCustomerId(loginUser);
        Common.setPoCreatedFields(commentPo, loginUser, loginUsername);
        ReturnObject ret_insert = commentDao.insertComment(commentPo);
        if (ret_insert.getCode().equals(0)) {
            CommentRetVo commentRetVo = (CommentRetVo) Common.cloneVo(commentPo, CommentRetVo.class);
            return new ReturnObject(commentRetVo);
        }
        return ret_insert;
    }

    /**
     * 分页查询sku下所有已通过审核的评论
     *
     * @param skuId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<Object>> selectAllPassCommentBySkuId(Long skuId, Integer pageNum, Integer pageSize) {
        ReturnObject<List<CommentPo>> ret = commentDao.selectAllPassCommentBySkuId(skuId, pageNum, pageSize);
        if (ret.getCode() != ResponseCode.OK) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "该商品不存在");
        }
        List<VoObject> commentRetVos = new ArrayList<>();
        for (CommentPo po : ret.getData()) {
            var customer = customerService.getCustomer(po.getCustomerId());

            //var customer=customerServiceMock.getCustomer(po.getCustomerId());
            CommentRetVo vo = new CommentRetVo(po, customer.getUserName(), customer.getRealName());
            commentRetVos.add(vo);
        }

        PageInfo<VoObject> commentRetVoPageInfo = PageInfo.of(commentRetVos);
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
    public ReturnObject confirmCommnets(Long did, Long id, CommentConclusionVo conclusion) {
        if (did != 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        Comment comment = new Comment();
        comment.setId(id);
        comment.setState(conclusion.getConclusion() == true ? Comment.State.PASS.getCode() : Comment.State.FORBID.getCode());
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
    public ReturnObject<PageInfo<VoObject>> selectAllCommentsOfUser(Long userId, Integer pageNum, Integer pageSize) {
        List<CommentPo> commentPos = commentDao.selectAllCommentsOfUser(userId, pageNum, pageSize);
        List<VoObject> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            var customer = customerService.getCustomer(po.getCustomerId());
            //var customer=customerServiceMock.getCustomer(po.getCustomerId());
            CommentRetVo vo = new CommentRetVo(po, customer.getUserName(), customer.getRealName());
            commentRetVos.add(vo);
        }

        //分页查询
        PageInfo<VoObject> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }

    /**
     * 管理员查看未审核/已审核评论列表(有疑问)
     *
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ReturnObject<PageInfo<VoObject>> selectCommentsOfState(Long did, Integer state, Integer pageNum, Integer pageSize) {
        if (did != 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }

        List<CommentPo> commentPos = commentDao.selelctCommentsOfState(state.byteValue(), pageNum, pageSize);
        List<VoObject> commentRetVos = new ArrayList<>();
        for (CommentPo po : commentPos) {
            var customer = customerService.getCustomer(po.getCustomerId());
            //var customer=customerServiceMock.getCustomer(po.getCustomerId());
            CommentRetVo vo = new CommentRetVo(po, customer.getUserName(), customer.getRealName());
            commentRetVos.add(vo);
        }

        PageInfo<VoObject> commentRetVoPageInfo = PageInfo.of(commentRetVos);
        CommentSelectRetVo commentSelectRetVo = new CommentSelectRetVo();
        commentSelectRetVo.setPage(pageNum.longValue());
        commentSelectRetVo.setPageSize(pageSize.longValue());
        commentSelectRetVo.setPages((long) commentRetVoPageInfo.getPages());
        commentSelectRetVo.setTotal(commentSelectRetVo.getTotal());
        commentSelectRetVo.setList(commentRetVos);

        return new ReturnObject<>(commentRetVoPageInfo);
    }


}

