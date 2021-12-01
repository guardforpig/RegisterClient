package cn.edu.xmu.oomall.wechatpay.mapper;

import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPoExample;
import java.util.List;

public interface WeChatPayRefundPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    int insert(WeChatPayRefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    int insertSelective(WeChatPayRefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    List<WeChatPayRefundPo> selectByExample(WeChatPayRefundPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    WeChatPayRefundPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(WeChatPayRefundPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_wechatpay_refund
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(WeChatPayRefundPo record);
}