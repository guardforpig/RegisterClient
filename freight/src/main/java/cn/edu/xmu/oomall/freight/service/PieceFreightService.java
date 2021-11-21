package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.dao.PieceFreightDao;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreightBo;
import cn.edu.xmu.oomall.freight.model.po.FreightModelPo;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightRetVo;
import cn.edu.xmu.oomall.freight.model.vo.PieceFreightVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yitong  Gao
 */
@Service
public class PieceFreightService {
    @Autowired
    private PieceFreightDao pieceFreightDao;
    private final Byte PieceFreightType=1;
    /**
     * 新增件数运费模板
     * @param createName
     * @param createId
     * @param id
     * @param pieceFreightVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addPieceFreight(String createName, Long createId, Long id, PieceFreightVo pieceFreightVo) {
        PieceFreightBo pieceFreightBo = (PieceFreightBo) Common.cloneVo(pieceFreightVo, PieceFreightBo.class);
        pieceFreightBo.setFreightModelId(id);
        ReturnObject judgeRegionId=pieceFreightDao.judgeRegionId(pieceFreightBo.getRegionId());
        if(!(boolean)judgeRegionId.getData())
        {
            return new ReturnObject(ReturnNo.FREIGHT_REGIONEXIST,"运费模板中该地区已经定义");

        }
        ReturnObject returnObject1=pieceFreightDao.getFreightModelById(pieceFreightBo.getFreightModelId());
        FreightModelPo freightModelPo=(FreightModelPo)returnObject1.getData();
        if(freightModelPo==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"操作的资源id不存在");
        }
        if(!freightModelPo.getType().equals(PieceFreightType))
        {
            return new ReturnObject(ReturnNo.FREIGHT_WRONGTYPE,"该运费模板类型与内容不符");

        }

        ReturnObject returnObject = pieceFreightDao.addPieceFreight(pieceFreightBo, createId, createName);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        PieceFreightBo pieceFreightBo1 = (PieceFreightBo) returnObject.getData();
        PieceFreightRetVo pieceFreightRetVo = (PieceFreightRetVo) Common.cloneVo(pieceFreightBo1, PieceFreightRetVo.class);
        return new ReturnObject(pieceFreightRetVo);
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     *
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject getPieceFreight(Long id, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<PieceFreightBo>> pageInfoReturnObject = pieceFreightDao.getPieceFreight(id, page, pageSize);
        if (pageInfoReturnObject.getData() == null) {
            return pageInfoReturnObject;
        }
        PageInfo<PieceFreightBo> pieceFreightBoPageInfo = pageInfoReturnObject.getData();
        List<PieceFreightRetVo> retList = new ArrayList<>();
        for (PieceFreightBo pieceFreightBo : pieceFreightBoPageInfo.getList()) {
            PieceFreightRetVo pieceFreightRetVo = (PieceFreightRetVo) Common.cloneVo(pieceFreightBo, PieceFreightRetVo.class);
            retList.add(pieceFreightRetVo);
        }
        PageInfo<PieceFreightRetVo> p = (PageInfo<PieceFreightRetVo>) Common.cloneVo(pieceFreightBoPageInfo, new PageInfo<PieceFreightRetVo>().getClass());
        p.setTotal(pieceFreightBoPageInfo.getTotal());
        p.setList(retList);
        return new ReturnObject(p);
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deletePieceFreight(Long id) {

        ReturnObject returnObject = pieceFreightDao.deletePieceFreight(id);
        return returnObject;
    }

    /**
     * 店家或管理员修改件数运费模板明细
     *
     * @param pieceFreightVo
     * @param id
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject modifyPieceFreight(PieceFreightVo pieceFreightVo, Long id, Long userId, String userName) {
        PieceFreightBo pieceFreightBo = (PieceFreightBo) Common.cloneVo(pieceFreightVo, PieceFreightBo.class);
        pieceFreightBo.setId(id);
        ReturnObject judgeRegionId=pieceFreightDao.judgeRegionId(pieceFreightBo.getRegionId());
        if(!(boolean)judgeRegionId.getData())
        {
            return new ReturnObject(ReturnNo.FREIGHT_REGIONSAME,"运费模板中该地区已经定义");

        }
        ReturnObject returnObject = pieceFreightDao.modifyPieceFreight(pieceFreightBo, userId, userName);
        return returnObject;

    }


}
