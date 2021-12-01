package cn.edu.xmu.oomall.wechatpay.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayRefundPoMapper;
import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayTransactionPoMapper;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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


}
