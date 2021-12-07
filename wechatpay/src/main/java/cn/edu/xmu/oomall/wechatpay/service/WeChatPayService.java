package cn.edu.xmu.oomall.wechatpay.service;

import cn.edu.xmu.oomall.wechatpay.dao.WeChatPayDao;
import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.vo.PaymentNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.RefundNotifyRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPrepayRetVo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

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
    public WeChatPayReturnObject createTransaction(WeChatPayTransaction weChatPayTransaction){
        WeChatPayTransaction transaction = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayTransaction.getOutTradeNo()).getData();
        if(transaction!=null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_TRADE_NO_USED);
        }

        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                WeChatPayReturnObject returnObject = paySuccess(weChatPayTransaction);
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
                WeChatPayReturnObject returnObject = payFail(weChatPayTransaction);
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
            default:
                break;
        }
        return new WeChatPayReturnObject(new WeChatPayPrepayRetVo());
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getTransaction(String outTradeNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getTransactionByOutTradeNo(outTradeNo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject closeTransaction(String outTradeNo){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(outTradeNo);
        weChatPayTransactionPo.setTradeState(TRADE_STATE_CLOSE);
        WeChatPayReturnObject returnObject = weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject createRefund(WeChatPayRefund weChatPayRefund){

        WeChatPayTransaction transaction = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        if( transaction!=null && (transaction.getTradeState().equals(TRADE_STATE_CLOSE)||transaction.getTradeState().equals(TRADE_STATE_FAIL)) ){
            return new WeChatPayReturnObject(WeChatPayReturnNo.USER_ACCOUNT_ABNORMAL);
        }
        weChatPayRefund.setPayerTotal(transaction.getPayerTotal());

        List<WeChatPayRefundPo> list = (List<WeChatPayRefundPo>) weChatPayDao.getRefundByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        int count=0;
        if(list!=null){
            for(WeChatPayRefundPo po:list){
                count += po.getPayerRefund();
            }
        }
        if(count+ weChatPayRefund.getRefund() > transaction.getPayerTotal()){
            return new WeChatPayReturnObject(WeChatPayReturnNo.USER_ACCOUNT_ABNORMAL);
        }

        WeChatPayReturnObject returnObject = null;
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
            default:
                break;
        }
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getRefund(String outRefundNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getRefundByOutRefundNo(outRefundNo);
        return returnObject;
    }


    private WeChatPayReturnObject paySuccess(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_SUCCESS);
        weChatPayTransaction.setPayerTotal(weChatPayTransaction.getTotal()-(int)(Math.random()*2));
        weChatPayTransaction.setSuccessTime(LocalDateTime.now());
        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private WeChatPayReturnObject payFail(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_FAIL);
        return weChatPayDao.createTransaction( (WeChatPayTransactionPo)cloneVo(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private WeChatPayReturnObject refundSuccess(WeChatPayRefund weChatPayRefund){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(weChatPayRefund.getOutTradeNo());
        weChatPayTransactionPo.setTradeState(TRADE_STATE_REFUND);
        weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);

        weChatPayRefund.setStatus(REFUND_STATUS_SUCCESS);
        weChatPayRefund.setPayerRefund(weChatPayRefund.getRefund());
        weChatPayRefund.setCreateTime(LocalDateTime.now());
        return weChatPayDao.createRefund( (WeChatPayRefundPo)cloneVo(weChatPayRefund,WeChatPayRefundPo.class) );
    }

    private WeChatPayReturnObject refundFail(WeChatPayRefund weChatPayRefund){
        weChatPayRefund.setStatus(REFUND_STATUS_FAIL);
        weChatPayRefund.setCreateTime(LocalDateTime.now());
        return weChatPayDao.createRefund( (WeChatPayRefundPo)cloneVo(weChatPayRefund,WeChatPayRefundPo.class) );
    }

}
