package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayTransactionVo;
import cn.edu.xmu.oomall.wechatpay.service.WeChatPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static cn.edu.xmu.oomall.core.util.Common.processFieldErrors;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class WeChatPayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    public Object createTransaction(@Validated @RequestBody WeChatPayTransactionVo weChatPayTransactionVo, BindingResult bindingResult){

        Object object = processFieldErrors(bindingResult, httpServletResponse);
        if (object != null){
            return object;
        }

        ReturnObject returnObject = weChatPayService.createTransaction(new WeChatPayTransaction(weChatPayTransactionVo));
        return Common.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    public Object getTransaction(@PathVariable("out_trade_no") String outTradeNo, @RequestParam("mchid") String mchid){

        ReturnObject returnObject = weChatPayService.getTransaction(outTradeNo,mchid);
        returnObject = Common.getRetObject(returnObject);
        return Common.decorateReturnObject(returnObject);
    }

}
