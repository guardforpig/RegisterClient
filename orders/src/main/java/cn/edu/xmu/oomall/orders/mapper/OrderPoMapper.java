package cn.edu.xmu.oomall.orders.mapper;

import cn.edu.xmu.oomall.orders.model.po.OrderPo;
import cn.edu.xmu.oomall.orders.model.po.OrderPoExample;
import java.util.List;

public interface OrderPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    int insert(OrderPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    int insertSelective(OrderPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    List<OrderPo> selectByExample(OrderPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    OrderPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OrderPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OrderPo record);
}