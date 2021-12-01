package cn.edu.xmu.oomall.wechatpay.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.dao.WeChatPayDao;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPrepayRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Service
@AutoConfigureMockMvc
public class WeChatPayService {

    private static final String TRADE_TYPE = String.format("JSAPI");
    private static final String TRADE_STATE_SUCCESS = String.format("SUCCESS");
    private static final String TRADE_STATE_FAIL = String.format("NOTPAY");

    @Autowired
    private WeChatPayDao weChatPayDao;

    @Autowired
    private MockMvc mvc;

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject createTransaction(WeChatPayTransaction weChatPayTransaction){

        weChatPayTransaction.setPrepayId(Common.genSeqNum());
        weChatPayTransaction.setTransactionId(Common.genSeqNum());
        weChatPayTransaction.setTradeType(TRADE_TYPE);

        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                ReturnObject returnObject = paySuccess(weChatPayTransaction);
                if(returnObject.getData()!=null) {
                    payNotify(returnObject.getData());
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
                    payNotify(returnObject.getData());
                }
                break;
            }
            case 3:
            {
                payFail(weChatPayTransaction);
                break;
            }
        }

        return new ReturnObject(new WeChatPayPrepayRetVo(weChatPayTransaction.getPrepayId()));
    }

    private ReturnObject paySuccess(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_SUCCESS);
        weChatPayTransaction.setTradeStateDesc("支付成功");
        weChatPayTransaction.setPayerTotal(weChatPayTransaction.getTotal());
        weChatPayTransaction.setPayerCurrency(weChatPayTransaction.getCurrency());
        weChatPayTransaction.setSuccessTime(LocalDateTime.now());

        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)Common.cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private ReturnObject payFail(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_FAIL);
        weChatPayTransaction.setTradeStateDesc("支付失败");

        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)Common.cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private void payNotify(WeChatPayTransaction weChatPayTransaction){
        //todo
    }

}
