package cn.edu.xmu.oomall.wechatpay.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.dao.WeChatPayDao;
import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.microservice.vo.PaymentNotifyVo;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.vo.PaymentNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPrepayRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Service
public class WeChatPayService {

    private static final String TRADE_STATE_SUCCESS = String.format("SUCCESS");
    private static final String TRADE_STATE_FAIL = String.format("NOTPAY");
    private static final String TRADE_STATE_CLOSE = String.format("CLOSED");

    @Autowired
    private WeChatPayDao weChatPayDao;

    @Autowired
    private WeChatPayNotifyService weChatPayNotifyService;


    @Transactional(rollbackFor=Exception.class)
    public ReturnObject createTransaction(WeChatPayTransaction weChatPayTransaction){
        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                ReturnObject returnObject = paySuccess(weChatPayTransaction);
                if(returnObject.getData()!=null) {
                    weChatPayNotifyService.paymentNotify(new PaymentNotifyRetVo( (WeChatPayTransaction) returnObject.getData() ));
                }
                break;
            }
            case 1:
            {
                paySuccess(weChatPayTransaction);
                break;
            }
            case 2:
            {
                ReturnObject returnObject = payFail(weChatPayTransaction);
                if(returnObject.getData()!=null) {
                    weChatPayNotifyService.paymentNotify(new PaymentNotifyRetVo( (WeChatPayTransaction) returnObject.getData() ));
                }
                break;
            }
            case 3:
            {
                payFail(weChatPayTransaction);
                break;
            }
        }
        return new ReturnObject(new WeChatPayPrepayRetVo());
    }

    private ReturnObject paySuccess(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_SUCCESS);
        weChatPayTransaction.setPayerTotal(weChatPayTransaction.getTotal());
        weChatPayTransaction.setSuccessTime(LocalDateTime.now());
        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)Common.cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private ReturnObject payFail(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_FAIL);
        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)Common.cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }


    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getTransaction(String outTradeNo){
        ReturnObject returnObject = weChatPayDao.getTransaction(outTradeNo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject closeTransaction(String outTradeNo){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(outTradeNo);
        weChatPayTransactionPo.setTradeState(TRADE_STATE_CLOSE);
        ReturnObject returnObject = weChatPayDao.closeTransaction(weChatPayTransactionPo);
        return returnObject;
    }

}
