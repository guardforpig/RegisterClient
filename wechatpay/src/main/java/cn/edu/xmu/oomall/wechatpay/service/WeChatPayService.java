package cn.edu.xmu.oomall.wechatpay.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.dao.WeChatPayDao;
import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.vo.PaymentNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.RefundNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPrepayRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Service
public class WeChatPayService {

    private static final String TRADE_STATE_SUCCESS = String.format("SUCCESS");
    private static final String TRADE_STATE_FAIL = String.format("NOTPAY");
    private static final String TRADE_STATE_CLOSE = String.format("CLOSED");
    private static final String TRADE_STATE_REFUND = String.format("REFUND");
    private static final String REFUND_STATUS_SUCCESS = String.format("SUCCESS");
    private static final String REFUND_STATUS_FAIL = String.format("ABNORMAL");

    @Autowired
    private WeChatPayDao weChatPayDao;

    @Autowired
    private WeChatPayNotifyService weChatPayNotifyService;


    @Transactional(rollbackFor=Exception.class)
    public ReturnObject createTransaction(WeChatPayTransaction weChatPayTransaction){
        WeChatPayTransaction wcpt = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayTransaction.getOutTradeNo()).getData();
        if(wcpt!=null){
            return new ReturnObject();//todo
        }

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
        ReturnObject returnObject = weChatPayDao.getTransactionByOutTradeNo(outTradeNo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject closeTransaction(String outTradeNo){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(outTradeNo);
        weChatPayTransactionPo.setTradeState(TRADE_STATE_CLOSE);
        ReturnObject returnObject = weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject createRefund(WeChatPayRefund weChatPayRefund){

        WeChatPayTransaction wcpt = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        if( wcpt!=null && !(wcpt.getTradeState().equals(TRADE_STATE_CLOSE)||wcpt.getTradeState().equals(TRADE_STATE_FAIL)) ){
            return new ReturnObject();//todo
        }
        weChatPayRefund.setPayerTotal(wcpt.getPayerTotal());

        List<WeChatPayRefundPo> list = (List<WeChatPayRefundPo>) weChatPayDao.getRefundByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        int count=0;
        if(list!=null){
            for(WeChatPayRefundPo po:list){
                count += po.getPayerRefund();
            }
        }
        if(count>=wcpt.getPayerTotal()){
            return new ReturnObject();//todo
        }

        ReturnObject returnObject = null;
        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                returnObject = refundSuccess(weChatPayRefund);
                if(returnObject.getData()!=null) {
                    weChatPayNotifyService.refundNotify(new RefundNotifyRetVo( (WeChatPayRefund) returnObject.getData() ));
                }
                break;
            }
            case 1:
            {
                returnObject = refundSuccess(weChatPayRefund);
                break;
            }
            case 2:
            {
                returnObject = refundFail(weChatPayRefund);
                if(returnObject.getData()!=null) {
                    weChatPayNotifyService.refundNotify(new RefundNotifyRetVo( (WeChatPayRefund) returnObject.getData() ));
                }
                break;
            }
            case 3:
            {
                returnObject = refundFail(weChatPayRefund);
                break;
            }
        }
        return returnObject;
    }

    private ReturnObject refundSuccess(WeChatPayRefund weChatPayRefund){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(weChatPayRefund.getOutTradeNo());
        weChatPayTransactionPo.setTradeState(TRADE_STATE_REFUND);
        weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);

        weChatPayRefund.setStatus(REFUND_STATUS_SUCCESS);
        weChatPayRefund.setPayerRefund(weChatPayRefund.getRefund());
        weChatPayRefund.setCreateTime(LocalDateTime.now());
        return weChatPayDao.createRefund( (WeChatPayRefundPo)Common.cloneVo(weChatPayRefund,WeChatPayRefundPo.class) );
    }

    private ReturnObject refundFail(WeChatPayRefund weChatPayRefund){
        weChatPayRefund.setStatus(REFUND_STATUS_FAIL);
        weChatPayRefund.setCreateTime(LocalDateTime.now());
        return weChatPayDao.createRefund( (WeChatPayRefundPo)Common.cloneVo(weChatPayRefund,WeChatPayRefundPo.class) );
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getRefund(String outRefundNo){
        ReturnObject returnObject = weChatPayDao.getRefundByOutRefundNo(outRefundNo);
        return returnObject;
    }

}
