package cn.edu.xmu.oomall.activity.dao;


import cn.edu.xmu.oomall.activity.mapper.GroupOnActivityPoMapper;
import cn.edu.xmu.oomall.activity.model.bo.GroupOnActivity;
import cn.edu.xmu.oomall.activity.model.po.GroupOnActivityPo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

/**
 * @author Lin Jiyuan
 * @sn 30320192200032
 */
@Repository
public class GroupActivityDao {
    private Logger logger = LoggerFactory.getLogger(GroupActivityDao.class);

    @Autowired
    private GroupOnActivityPoMapper groupOnActivityPoMapper;



    /**
     * 新增参与团购的商品
     *
     * @return  Groupon对象列表
     */
    public ReturnObject<GroupOnActivity> getGroupOnActivity(long id){

        GroupOnActivityPo g1;
        try {
            g1 = groupOnActivityPoMapper.selectByPrimaryKey(id);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(g1==null)
        {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        GroupOnActivity groupOnActivity = (GroupOnActivity) cloneVo(g1,GroupOnActivity.class);
        return new ReturnObject<GroupOnActivity>(groupOnActivity);
    }


    /**
     * 删除团购
     * @param id 团购活动id
     * @return
     */
    public ReturnObject deleteGroupon(long id) {
        ReturnObject<Object> retObj = null;
        int ret;
        try {
            ret = groupOnActivityPoMapper.deleteByPrimaryKey(id);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if (ret == 0) {
            retObj = new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {
            retObj = new ReturnObject<>();
        }

        return retObj;
    }

    /**
     * 修改团购活动
     * @param groupOnActivity 传入的Groupon对象
     * @return 返回对象ReturnObj
     */
    public ReturnObject modifyGroupOnActivity(GroupOnActivity groupOnActivity)
    {
        ReturnObject retObj;
        GroupOnActivityPo groupOnActivityPo = (GroupOnActivityPo) cloneVo(groupOnActivity,GroupOnActivityPo.class);
       setPoModifiedFields(groupOnActivityPo,groupOnActivity.getModifierId(),groupOnActivity.getModifierName());
        int ret;
        try
        {
            ret = groupOnActivityPoMapper.updateByPrimaryKeySelective(groupOnActivityPo);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

        if (ret == 0){
            retObj = new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {

            retObj = new ReturnObject(ReturnNo.OK);
        }
        return retObj;
    }



}
