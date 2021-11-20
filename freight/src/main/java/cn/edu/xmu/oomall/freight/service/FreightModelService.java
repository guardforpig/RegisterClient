package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.FreightModelDao;
import cn.edu.xmu.oomall.freight.model.bo.FreightModel;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Service
public class FreightModelService {


    @Autowired
    FreightModelDao freightModelDao;


    /**
     * 管理员定义运费模板
     * @param freightModelInfo 运费模板资料
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 运费模板
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addFreightModel(FreightModelInfoVo freightModelInfo,
                                        Long userId, String userName){
        FreightModel freightModel = (FreightModel) Common.cloneVo(freightModelInfo,FreightModel.class);
        //新建,不为默认
        freightModel.setDefaultModel((byte) 0);
        //设置创建者
        Common.setPoCreatedFields(freightModel,userId,userName);
        //id置空
        freightModel.setId(null);
        return freightModelDao.addFreightModel(freightModel);
    }


    /**
     * 获得运费模板
     * @param name 模板名称
     * @param page 页
     * @param pageSize 页大小
     * @return 运费模板
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject showFreightModel(String name, Integer page, Integer pageSize){
        return freightModelDao.showFreightModel(name,page,pageSize);
    }


    /**
     * 管理员克隆运费模板
     * @param id 需要克隆的模板id
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 运费模板
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cloneFreightModel(Long id, Long userId, String userName){
        ReturnObject returnObjectToBeCloned=freightModelDao.showFreightModelById(id);
        //如果查不到,返回资源不存在
        if(returnObjectToBeCloned.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
            return returnObjectToBeCloned;
        }
        FreightModel freightModelToBeCloned= (FreightModel) returnObjectToBeCloned.getData();

        //设置创建人
        Common.setPoCreatedFields(freightModelToBeCloned,userId,userName);
        //将置空id
        freightModelToBeCloned.setId(null);
        Random r = new Random();
        //模板修改时间,修改人信息置空
        freightModelToBeCloned.setGmtModified(null);
        freightModelToBeCloned.setModifierId(null);
        freightModelToBeCloned.setModifierName(null);
        //模板名称为原加随机数
        freightModelToBeCloned.setName(freightModelToBeCloned.getName() + r.nextInt(10000000));
        //克隆的不是默认模板
        freightModelToBeCloned.setType((byte)0);
        //插入
        return freightModelDao.addFreightModel(freightModelToBeCloned);
    }

    /**
     * 获得默认运费模板
     * @return 默认运费模板
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getDefaultFreightModel() {
        return freightModelDao.getDefaultModel();
    }

    /**
     * 获得运费模板详情，查不到返回默认模板
     * @param id 运费模板id
     * @return 运费模板
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject showFreightModelById(Long id){

        ReturnObject returnObject=freightModelDao.showFreightModelById(id);
        //如果查不到
        if(returnObject.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
            return freightModelDao.getDefaultModel();
        }
        return returnObject;
    }

    /**
     * 管理员修改运费模板
     * @param freightModelInfo 运费模板资料
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 运费模板
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateFreightModel(Long id,FreightModelInfoVo freightModelInfo,
                                                              Long userId, String userName){
        FreightModel freightModel= (FreightModel) Common.cloneVo(freightModelInfo,FreightModel.class);
        freightModel.setId(id);
        Common.setPoModifiedFields(freightModel,userId,userName);
        //如果修改的是默认的模板，会将原来的取消，如果修改的是原来的默认模板，逻辑不变
        if(freightModelInfo.getDefaultModel().equals((byte)1))
        {
            return freightModelDao.setDefaultModel(freightModel);
        }
        return freightModelDao.updateFreightModel(freightModel);
    }

    /**
     * 管理员删除运费模板
     * @param id 运费模板id
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteFreightModel(Long id){
        ReturnObject returnObject=freightModelDao.getDefaultModel();
        //如果有默认模板，且删除的正是默认模板
        if(!returnObject.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)&&((FreightModel) returnObject.getData()).getId().equals(id))
        {
            //不能删默认模板
            return new ReturnObject(ReturnNo.FREIGHT_NOTDELETED);
        }
        return freightModelDao.deleteFreightModel(id);
    }

}