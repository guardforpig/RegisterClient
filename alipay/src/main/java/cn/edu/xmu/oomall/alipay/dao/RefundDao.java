package cn.edu.xmu.oomall.alipay.dao;

import cn.edu.xmu.oomall.alipay.mapper.AlipayRefundPoMapper;
import cn.edu.xmu.oomall.alipay.model.bo.Refund;
import cn.edu.xmu.oomall.alipay.model.po.AlipayPaymentPo;
import cn.edu.xmu.oomall.alipay.model.po.AlipayPaymentPoExample;
import cn.edu.xmu.oomall.alipay.model.po.AlipayRefundPo;
import cn.edu.xmu.oomall.alipay.model.po.AlipayRefundPoExample;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RefundDao {
    @Autowired
    private AlipayRefundPoMapper alipayRefundPoMapper;

    private Logger logger = LoggerFactory.getLogger(RefundDao.class);

    public List<AlipayRefundPo> selectRefundByOutTradeNo(String outTradeNo)
    {
        try{
            AlipayRefundPoExample alipayRefundPoExample = new AlipayRefundPoExample();
            AlipayRefundPoExample.Criteria criteria = alipayRefundPoExample.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<AlipayRefundPo> alipayRefundPoList= alipayRefundPoMapper.selectByExample(alipayRefundPoExample);
            return alipayRefundPoList;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Refund selectRefundByOutRequestNo(String outRequestNo)
    {
        try{
            AlipayRefundPoExample alipayRefundPoExample = new AlipayRefundPoExample();
            AlipayRefundPoExample.Criteria criteria = alipayRefundPoExample.createCriteria();
            criteria.andOutRequestNoEqualTo(outRequestNo);
            List<AlipayRefundPo> alipayRefundPoList= alipayRefundPoMapper.selectByExample(alipayRefundPoExample);
            if(alipayRefundPoList.size()==0)
            {
                return null;
            }
            return (Refund) Common.cloneVo(alipayRefundPoList.get(0),Refund.class);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }


    public boolean insertRefund(Refund refund)
    {
        try{
            AlipayRefundPo alipayRefundPo= (AlipayRefundPo) Common.cloneVo(refund,AlipayRefundPo.class);
            alipayRefundPoMapper.insertSelective(alipayRefundPo);
            return true;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return false;
        }
    }
}
