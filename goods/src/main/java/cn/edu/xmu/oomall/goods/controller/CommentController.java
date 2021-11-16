package cn.edu.xmu.oomall.goods.controller;
import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.model.vo.CommentConclusionVo;
import cn.edu.xmu.oomall.goods.model.vo.CommentVo;
import cn.edu.xmu.oomall.goods.service.CommentService;
import com.alibaba.nacos.api.common.ResponseCode;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 评论
 * @author Xinyu Jiang
 * @sn 22920192204219
 **/
@Api(value = "评论服务", tags = "comment")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HttpServletResponse httpServletResponse;


    /**
     * 获得评论所有状态
     */
    @ApiOperation(value = "获得评论的所有状态")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/comments/states")
    public Object getCommentStates(){
        ReturnObject ret=commentService.getCommentStates();
        return Common.decorateReturnObject(ret);
    }

    /**
     * 买家新增SKU的评论
     */
    @ApiOperation(value = "买家新增SKU的评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(paramType = "body", dataType = "CommentVo", name = "commentVo", value = "新增评论", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 903, message = "用户没有购买此商品")
    })
    @PostMapping("orderitems/{id}/comments")
    public Object addCommentOnSku(
            @PathVariable Long id,
            @Validated @RequestBody CommentVo commentVo,
            BindingResult bindingResult, Long loginUser,String loginUserName){
        //todo:
        loginUser=Long.valueOf(111);
        loginUserName="hhhhh";

        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }

        var ret = commentService.newComment(id,commentVo, loginUser,loginUserName);
        if(ret.getCode().equals(ResponseCode.OK))httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(ret);
    }

    /**
     * 查看products评价列表
     */
    @ApiOperation(value = "查看products评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="page", required = false, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="pageSize", required = false, dataType="Integer", paramType="query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/products/{id}/comments")
    public Object selectSkuComments(@PathVariable long id, @RequestParam(required = false,defaultValue = "1") Integer page, @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        ReturnObject ret=commentService.selectAllPassCommentBySkuId(id,page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 管理员核审评论
     */
    @ApiOperation(value = "管理员核审评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="did", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })      @ApiImplicitParam(paramType = "body", dataType = "CommentConclusionVo", name = "commentConclusionVo", value = "新增评论", required = true)
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("/shops/{did}/comments/{id}/confirm")
    public Object confirmComment(@PathVariable long did,@PathVariable long id,
                                 @Valid @RequestBody CommentConclusionVo commentConclusionVo,
                                 BindingResult bindingResult){
        var res =  Common.processFieldErrors(bindingResult, httpServletResponse);
        if(res != null){
            return null;
        }
        ReturnObject ret=commentService.confirmCommnets(did,id,commentConclusionVo);
        return Common.decorateReturnObject(ret);
    }

    /**
     * 买家查看自己的评价记录
     */
    @ApiOperation(value = "买家查看自己的评价记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="page", required = false, dataType="Integer", paramType="query"),
            @ApiImplicitParam(name="pageSize", required = false, dataType="Integer", paramType="query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/comments")
    public Object getOwnComments(
            Long uid,
            @RequestParam(required = false,defaultValue = "1") Integer page,
            @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        ReturnObject<PageInfo<VoObject>> ret=commentService.selectAllCommentsOfUser(uid,page,pageSize);
        return Common.getPageRetObject(ret);
    }

    /**
     * 管理员查看未核审的评论列表
     *
     */
    @ApiOperation(value = "管理员查看未核审/已核审的评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/shops/{id}/comments/all")
    @Audit
    public Object getAllComments(
            @PathVariable Long id,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false,defaultValue = "1") Integer page,
            @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        logger.debug("getAllComments: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject ret=commentService.selectCommentsOfState(id,state,page,pageSize);
        return Common.getPageRetObject(ret);
    }

}
