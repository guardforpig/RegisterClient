package cn.edu.xmu.oomall.freight.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.oomall.freight.service.PieceFreightService;
import cn.edu.xmu.privilegegateway.annotation.annotation.Audit;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginName;
import cn.edu.xmu.privilegegateway.annotation.annotation.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.decorateReturnObject;
import static cn.edu.xmu.oomall.core.util.Common.getPageRetObject;

/**
 * @author Yitong  Gao
 */
@Api(value = "件数运费模板API", tags = "件数运费模板API")
@RestController
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class PieceFreightController {
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Autowired
    private PieceFreightService pieceFreightService;

    /**
     * 管理员定义件数模板明细
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param pieceFreightVo
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "管理员定义件数模板明细",produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "PieceFreightVo", paramType = "body", dataType = "PieceFreightVo", value = "件数运费模板定义", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code =0, message = "成功"),
            @ApiResponse(code= 500,message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 996, message = "该运费模板类型与内容不符"),
            @ApiResponse(code = 997, message = "运费模板中该地区已经定义")})
    @Audit(departName="shops")
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object addPieceFreight(@LoginUser Long loginUserId, @LoginName String loginUserName, @PathVariable(value = "shopId", required = true) Long shopId,
                                  @PathVariable(value = "id", required = true) Long id,
                                  @Validated @RequestBody PieceFreightVo pieceFreightVo,
                                  BindingResult bindingResult) {

        Object obj = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (obj!=null) {
            return obj;
        }
        if(shopId!=0){
            return decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "非管理员无权操作"));
        }
        ReturnObject returnObject = pieceFreightService.addPieceFreight(loginUserName,loginUserId,id,pieceFreightVo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "店家或管理员查询件数运费模板的明细")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })

    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")})
    @Audit(departName="shops")
    @GetMapping(value = "/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object queryPieceFreight(
            @LoginUser Long loginUserId, @LoginName String loginUserName,
            @PathVariable(name = "shopId", required = true) Long shopId,
            @PathVariable(name = "id", required = true) Long id,
            @RequestParam(name = "page", required = false,defaultValue ="1") Integer page,
            @RequestParam(name = "pageSize",  required = false,defaultValue ="10") Integer pageSize) {
        ReturnObject returnObject=pieceFreightService.getPieceFreight(id,page,pageSize);
        return decorateReturnObject(getPageRetObject(returnObject));
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @return
     */
    @Audit(departName="shops")
    @ApiOperation(value = "店家或管理员删掉件数运费模板明细",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code= 500,message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")    })
    @Audit(departName="shops")
    @DeleteMapping("/shops/{shopId}/pieceItems/{id}")
    public Object deletePieceFreight(@LoginUser Long loginUserId, @LoginName String loginUserName, @PathVariable(value = "shopId", required = true) Long shopId,
                                     @PathVariable(value = "id", required = true) Long id)
    {
        if(shopId!=0){
            return decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "非管理员无权操作"));
        }
        ReturnObject returnObject = pieceFreightService.deletePieceFreight(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家或管理员修改件数运费模板明细
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param pieceFreightVo
     * @return
     */

    @ApiOperation(value = "店家或管理员修改件数运费模板明细",  produces="application/json;charset=UTF-8")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, dataType = "String", paramType = "header"),
            @ApiImplicitParam(name = "shopId", value = "商店id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "运费模板id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(paramType = "body", dataType = "Object", name = "body", value ="运费模板明细" ,required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功"),
            @ApiResponse(code= 500,message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 999, message = "运费模板中该地区已经定义"),

    })
    @Audit(departName="shops")
    @PutMapping("/shops/{shopId}/pieceItems/{id}")
    public Object modifyPieceFreight(@LoginUser Long loginUserId, @LoginName String loginUserName, @PathVariable(value = "shopId", required = true) Long shopId,
                                     @PathVariable(value = "id", required = true) Long id,
                                     @Validated @RequestBody PieceFreightVo pieceFreightVo){

        if(shopId!=0){
            return decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "非管理员无权操作"));
        }
        loginUserId=111L;
        loginUserName="admin";

        ReturnObject returnObject = pieceFreightService.modifyPieceFreight(pieceFreightVo,id,loginUserId, loginUserName);

        return Common.decorateReturnObject(returnObject);
    }
}

