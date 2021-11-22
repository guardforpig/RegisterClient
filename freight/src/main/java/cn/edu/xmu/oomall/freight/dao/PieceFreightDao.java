package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;

import cn.edu.xmu.oomall.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.oomall.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreight;
import cn.edu.xmu.oomall.freight.model.po.FreightModelPo;
import cn.edu.xmu.oomall.freight.model.po.PieceFreightPo;
import cn.edu.xmu.oomall.freight.model.po.PieceFreightPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yitong  Gao
 */
@Repository
public class PieceFreightDao {
    private static final Logger logger = LoggerFactory.getLogger(PieceFreightDao.class);

    @Autowired
    private PieceFreightPoMapper pieceFreightPoMapper;
    @Autowired
    private FreightModelPoMapper freightModelPoMapper;

    /**
     * 新增件数运费模板
     *
     * @param pieceFreight
     * @return
     */
    public ReturnObject addPieceFreight(PieceFreight pieceFreight, Long createId, String createName) {
        try {
            PieceFreightPo pieceFreightPo = (PieceFreightPo) Common.cloneVo(pieceFreight, PieceFreightPo.class);
            Common.setPoCreatedFields(pieceFreightPo, createId, createName);
            if (pieceFreight.getRegionId() != null) {
                PieceFreightPoExample example = new PieceFreightPoExample();
                PieceFreightPoExample.Criteria criteria = example.createCriteria();
                criteria.andRegionIdEqualTo(pieceFreight.getRegionId());
                List<PieceFreightPo> list = pieceFreightPoMapper.selectByExample(example);
                if (list != null && list.size() > 0) {
                    return new ReturnObject(ReturnNo.FREIGHT_REGIONEXIST);
                }
            }
            pieceFreightPoMapper.insertSelective(pieceFreightPo);
            PieceFreightPo newPieceFreightPo = pieceFreightPoMapper.selectByPrimaryKey(pieceFreightPo.getId());
            return new ReturnObject((Common.cloneVo(newPieceFreightPo, PieceFreight.class)));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员查询件数运费模板的明细
     *
     * @param freightModelId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getPieceFreight(Long freightModelId, Integer page, Integer pageSize) {
        try {
            PieceFreightPoExample example = new PieceFreightPoExample();
            PieceFreightPoExample.Criteria criteria = example.createCriteria();
            criteria.andFreightModelIdEqualTo(freightModelId);
            PageHelper.startPage(page, pageSize);
            List<PieceFreightPo> list = pieceFreightPoMapper.selectByExample(example);
            if (list == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PageInfo pageInfo = new PageInfo(list);
            List<PieceFreight> pieceFreights = new ArrayList<>();
            for (PieceFreightPo pieceFreightPo : list) {
                PieceFreight pieceFreight = (PieceFreight) Common.cloneVo(pieceFreightPo, PieceFreight.class);
                pieceFreights.add(pieceFreight);
            }
            pageInfo.setList(pieceFreights);
            return new ReturnObject(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     *
     * @param id
     * @return
     */
    public ReturnObject deletePieceFreight(Long id) {
        try {
            int ret = pieceFreightPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员修改件数运费模板明细
     *
     * @param pieceFreight
     * @param userId
     * @param userName
     * @return
     */
    public ReturnObject updatePieceFreight(PieceFreight pieceFreight, Long userId, String userName) {
        try {
            PieceFreightPo pieceFreightPo = (PieceFreightPo) Common.cloneVo(pieceFreight, PieceFreightPo.class);
            Common.setPoModifiedFields(pieceFreightPo, userId, userName);
            PieceFreightPo rp = pieceFreightPoMapper.selectByPrimaryKey(pieceFreight.getId());
            if (rp == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if (pieceFreight.getRegionId() != null) {
                PieceFreightPoExample example = new PieceFreightPoExample();
                PieceFreightPoExample.Criteria criteria = example.createCriteria();
                criteria.andRegionIdEqualTo(pieceFreight.getRegionId());
                List<PieceFreightPo> list = pieceFreightPoMapper.selectByExample(example);
                if (list != null && list.size() > 0) {
                    if (!list.get(0).getId().equals(pieceFreightPo.getId())) {
                        return new ReturnObject(ReturnNo.FREIGHT_REGIONSAME);
                    }
                }
            }
            pieceFreightPoMapper.updateByPrimaryKeySelective(pieceFreightPo);
            return new ReturnObject();

        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
