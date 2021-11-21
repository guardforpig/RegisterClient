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

import java.time.LocalDateTime;

/**
 * @author jiyuan lin
 * @date 2021/11/14
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
        GroupOnActivity groupOnActivity = (GroupOnActivity) Common.cloneVo(g1,GroupOnActivity.class);
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
        ReturnObject<Object> retObj = null;
        GroupOnActivityPo groupOnActivityPo = (GroupOnActivityPo) Common.cloneVo(groupOnActivity,GroupOnActivityPo.class);
        Common.setPoModifiedFields(groupOnActivityPo,groupOnActivity.getModifiedBy(),groupOnActivity.getModiName());
        groupOnActivityPo.setGmtModified(LocalDateTime.now());
        int ret;
        try
        {
            ret = groupOnActivityPoMapper.updateByPrimaryKeySelective(groupOnActivityPo);
        }
        catch(Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }

        if (ret == 0){
            retObj = new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        } else {

            retObj = new ReturnObject();
        }
        return retObj;
    }



}
