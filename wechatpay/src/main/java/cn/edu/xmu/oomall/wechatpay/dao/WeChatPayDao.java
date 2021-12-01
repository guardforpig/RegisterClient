package cn.edu.xmu.oomall.wechatpay.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayRefundPoMapper;
import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayTransactionPoMapper;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPoExample;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Repository
public class WeChatPayDao {

    @Autowired
    private WeChatPayTransactionPoMapper weChatPayTransactionPoMapper;

    @Autowired
    private WeChatPayRefundPoMapper weChatPayRefundPoMapper;


    public ReturnObject createTransaction(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            weChatPayTransactionPoMapper.insertSelective(weChatPayTransactionPo);
            WeChatPayTransactionPo newWeChatPayTransactionPo = weChatPayTransactionPoMapper.selectByPrimaryKey(weChatPayTransactionPo.getId());
            return new ReturnObject(Common.cloneVo(newWeChatPayTransactionPo, WeChatPayTransaction.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getTransactionByOutTradeNo(String outTradeNo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayTransactionPo> list = weChatPayTransactionPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(Common.cloneVo(list.get(0), WeChatPayTransaction.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject updateTransactionByOutTradeNo(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(weChatPayTransactionPo.getOutTradeNo());
            int ret = weChatPayTransactionPoMapper.updateByExampleSelective(weChatPayTransactionPo,example);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject createRefund(WeChatPayRefundPo weChatPayRefundPo){
        try{
            weChatPayRefundPoMapper.insertSelective(weChatPayRefundPo);
            WeChatPayRefundPo newWeChatPayRefundPo = weChatPayRefundPoMapper.selectByPrimaryKey(weChatPayRefundPo.getId());
            return new ReturnObject(Common.cloneVo(newWeChatPayRefundPo, WeChatPayRefund.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getRefundByOutTradeNo(String outTradeNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            return new ReturnObject(list);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getRefundByOutRefundNo(String outRefundNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutRefundNoEqualTo(outRefundNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(Common.cloneVo(list.get(0), WeChatPayRefund.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
