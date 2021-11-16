package cn.edu.xmu.oomall.orders.mapper;

import cn.edu.xmu.oomall.orders.model.po.ErrorPaymentPo;
import cn.edu.xmu.oomall.orders.model.po.ErrorPaymentPoExample;
import java.util.List;

public interface ErrorPaymentPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    int insert(ErrorPaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    int insertSelective(ErrorPaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    List<ErrorPaymentPo> selectByExample(ErrorPaymentPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    ErrorPaymentPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ErrorPaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_error_payment
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ErrorPaymentPo record);
}