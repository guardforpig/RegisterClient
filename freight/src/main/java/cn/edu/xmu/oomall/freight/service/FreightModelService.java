package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.FreightModelDao;
import cn.edu.xmu.oomall.freight.dao.PieceFreightDao;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import cn.edu.xmu.oomall.freight.dao.WeightFreightDao;
import cn.edu.xmu.oomall.freight.model.bo.FreightModel;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreight;
import cn.edu.xmu.oomall.freight.model.bo.Region;
import cn.edu.xmu.oomall.freight.model.bo.WeightFreight;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPo;
import cn.edu.xmu.oomall.freight.model.vo.FreightCalculatingPostVo;
import cn.edu.xmu.oomall.freight.model.vo.FreightCalculatingRetVo;
import cn.edu.xmu.oomall.freight.model.vo.FreightModelInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Service
public class FreightModelService {


    @Autowired
    FreightModelDao freightModelDao;

    @Autowired
    WeightFreightDao weightFreightDao;
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

        //如果是默认模板需要把原来默认模板改为非默认
        if(freightModel.getDefaultModel().equals((byte)1))
        {
            freightModelDao.deleteDefaultFreight();
        }
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
        //如果name非空那就用name筛选
        if (name != null && !"".equals(name)) {
            return freightModelDao.selectFreightModelByName(name,page,pageSize);
        }
        else
        {
            return freightModelDao.selectAllFreightModel(page,pageSize);
        }
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
        ReturnObject returnObjectToBeCloned=freightModelDao.selectFreightModelById(id);
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

        ReturnObject returnObject1=weightFreightDao.getAllWeightItems(id);

        //克隆freightItem
        if (returnObject1.getCode().equals(ReturnNo.OK))
        {
            List<WeightFreight> weightFreightList= (List<WeightFreight>) returnObject1.getData();
            for (WeightFreight weightFreight:weightFreightList)
            {
                //设置创建人
                Common.setPoCreatedFields(weightFreight,userId,userName);
                //将置空id
                weightFreight.setId(null);
                weightFreight.setFreightModelId(freightModelToBeCloned.getId());
                //模板修改时间,修改人信息置空
                weightFreight.setGmtModified(null);
                weightFreight.setModifierId(null);
                weightFreight.setModifierName(null);
                weightFreightDao.addWeightItems((WeightFreightPo) Common.cloneVo(weightFreight, WeightFreightPo.class),userId,userName);
            }
        }

        //插入
        return freightModelDao.addFreightModel(freightModelToBeCloned);
    }

    /**
     * 获得默认运费模板
     * @return 默认运费模板
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getDefaultFreightModel() {
        return freightModelDao.getDefaultFreight();
    }

    /**
     * 获得运费模板详情，查不到返回默认模板
     * @param id 运费模板id
     * @return 运费模板
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject showFreightModelById(Long id){

        ReturnObject returnObject=freightModelDao.selectFreightModelById(id);
        //如果查不到
        if(returnObject.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
            return freightModelDao.getDefaultFreight();
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
            freightModelDao.deleteDefaultFreight();
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
        ReturnObject returnObject=freightModelDao.getDefaultFreight();
        //如果有默认模板，且删除的正是默认模板
        if(!returnObject.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)&&((FreightModel) returnObject.getData()).getId().equals(id))
        {
            //不能删默认模板
            return new ReturnObject(ReturnNo.FREIGHT_NOTDELETED);
        }

        ReturnObject returnObject1=weightFreightDao.getAllWeightItems(id);

        //删除freightItem
        if (returnObject1.getCode().equals(ReturnNo.OK))
        {
            List<WeightFreight> weightFreightList= (List<WeightFreight>) returnObject1.getData();
            for (WeightFreight weightFreight:weightFreightList)
            {
                weightFreightDao.deleteWeightItems(weightFreight.getId());
            }
        }
        return freightModelDao.deleteFreightModel(id);
    }


    /**
     * 计算一批货品的运费
     *
     * @param rid   地区id
     * @param items 要计算的货品的信息
     * @return ReturnObject
     */
    @Transactional(readOnly = true)
    public ReturnObject calculateFreight(Long rid, List<FreightCalculatingPostVo> items) {
        Long freightPrice = 0L;
        Long productId = 0L;
        ReturnObject regionsRet = regionDao.getParentRegion(rid);
        if (!regionsRet.getCode().equals(ReturnNo.OK)) {
            return regionsRet;
        }
        var regions = (List<Region>) regionsRet.getData();
        var regionIds = regions.stream().map(Region::getId).collect(Collectors.toList());
        regionIds.add(rid);

        Integer sumQuantity = 0;
        Integer sumWeight = 0;
        for (var item : items) {
            sumQuantity += item.getQuantity();
            sumWeight += item.getWeight() * item.getQuantity();
        }
        for (var item : items) {
            Long fid = item.getFreightId();
            ReturnObject freightRet = getFreightModelById(fid);
            if (!freightRet.getCode().equals(ReturnNo.OK)) {
                return freightRet;
            }
            var freightModel = (FreightModel) freightRet.getData();
            if (freightModel.getType() == 0) {
                ReturnObject weightFreightRet = weightFreightDao.getWeightItem(fid, regionIds);
                if (!weightFreightRet.getCode().equals(ReturnNo.OK)) {
                    return weightFreightRet;
                }
                Long newPrice = calculateWeightFright(sumWeight, (WeightFreight) weightFreightRet.getData(), freightModel.getUnit());
                if (newPrice > freightPrice) {
                    freightPrice = newPrice;
                    productId = item.getProductId();
                }
            } else {
                ReturnObject pieceFreightRet = pieceFreightDao.getPieceItem(fid, regionIds);
                if (!pieceFreightRet.getCode().equals(ReturnNo.OK)) {
                    return pieceFreightRet;
                }
                Long newPrice = calculatePieceFright(sumQuantity, (PieceFreight) pieceFreightRet.getData(), freightModel.getUnit());
                if (newPrice > freightPrice) {
                    freightPrice = newPrice;
                    productId = item.getProductId();
                }
            }
        }
        return new ReturnObject(new FreightCalculatingRetVo(freightPrice, productId));
    }

    private Long calculatePieceFright(Integer quantity, PieceFreight pieceFreight, Integer unit) {
        var firstItem = pieceFreight.getFirstItems();
        var firstItemPrice = pieceFreight.getFirstItemFreight();
        var additionalItemsPrice = pieceFreight.getAdditionalItemsPrice();
        return firstItemPrice + calculatePart(firstItem, null, quantity, unit, additionalItemsPrice);
    }

    private Long calculateWeightFright(Integer weight, WeightFreight weightFreight, Integer unit) {
        var firstWeight = weightFreight.getFirstWeight();
        var firstWeightFreight = weightFreight.getFirstWeightFreight();
        var tenPrice = weightFreight.getTenPrice();
        var fiftyPrice = weightFreight.getFiftyPrice();
        var hundredPrice = weightFreight.getHundredPrice();
        var trihunPrice = weightFreight.getTrihunPrice();
        var abovePrice = weightFreight.getAbovePrice();
        return firstWeightFreight +
                calculatePart(firstWeight, 10_000, weight, unit, tenPrice) +
                calculatePart(10_000, 50_000, weight, unit, fiftyPrice) +
                calculatePart(50_000, 100_000, weight, unit, hundredPrice) +
                calculatePart(100_000, 300_000, weight, unit, trihunPrice) +
                calculatePart(300_000, null, weight, unit, abovePrice)
                ;
    }

    private Long calculatePart(Integer level, Integer nextLevel, Integer weight, Integer unit, Long price) {
        if (weight <= level) {
            return 0L;
        } else if (Objects.isNull(nextLevel) || weight <= nextLevel) {
            return (weight - level + unit - 1) / unit * price;
        } else {
            return (nextLevel - level + unit - 1) / unit * price;
        }
    }
}