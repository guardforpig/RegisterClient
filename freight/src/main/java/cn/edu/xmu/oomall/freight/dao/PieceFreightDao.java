package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.oomall.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreightBo;
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
     * @param pieceFreightBo
     * @return
     */
    public ReturnObject addPieceFreight(PieceFreightBo pieceFreightBo, Long createId,String createName){
        PieceFreightPo pieceFreightPo = (PieceFreightPo) Common.cloneVo(pieceFreightBo, PieceFreightPo.class);
        Common.setPoCreatedFields(pieceFreightPo, createId, createName);
        Common.setPoModifiedFields(pieceFreightPo, createId, createName);
        try {
            if (pieceFreightPoMapper.insert(pieceFreightPo) == 1) {
                PieceFreightBo pieceFreightBo1 = (PieceFreightBo) Common.cloneVo(pieceFreightPo,PieceFreightBo.class);
                return new ReturnObject(pieceFreightBo1);
            } else {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 判断运费模板中该地区是否已经定义
     * @param regionId
     * @return
     */
    public ReturnObject judgeRegionId(Long regionId)
    {

        try {
            PieceFreightPoExample example = new PieceFreightPoExample();
            PieceFreightPoExample.Criteria criteria = example.createCriteria();
            criteria.andRegionIdEqualTo(regionId);
            List<PieceFreightPo> list = pieceFreightPoMapper.selectByExample(example);
            if (list.size() != 0) {
                return new ReturnObject(false);
            } else {
                return new ReturnObject(true);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 根据id查询运费模板
     * @param freightModelId
     * @return
     */
    public ReturnObject getFreightModelById(Long freightModelId)
    {
        try {
            FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(freightModelId);
            return new ReturnObject(freightModelPo);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    /**
     * 店家或管理员查询件数运费模板的明细
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getPieceFreight(Long id,Integer page,Integer pageSize)
    {
        PieceFreightPoExample example = new PieceFreightPoExample();
        PieceFreightPoExample.Criteria criteria = example.createCriteria();
        try {
            criteria.andFreightModelIdEqualTo(id);
            PageHelper.startPage(page, pageSize);
            List<PieceFreightPo> list = pieceFreightPoMapper.selectByExample(example);
            if(list==null)
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PageInfo pageInfo = new PageInfo(list);
            List<PieceFreightBo> pieceFreightBos=new ArrayList<>();
            for(PieceFreightPo pieceFreightPo:list)
            {
                PieceFreightBo pieceFreightBo=(PieceFreightBo) Common.cloneVo(pieceFreightPo,PieceFreightBo.class);
                pieceFreightBos.add(pieceFreightBo);
            }
            pageInfo.setList(pieceFreightBos);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员删掉件数运费模板明细
     * @param id
     * @return
     */
    public ReturnObject deletePieceFreight(Long id)
    {
        try {
            if(pieceFreightPoMapper.deleteByPrimaryKey(id)==1)
            {
                return new ReturnObject();
            }
            else
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    /**
     * 店家或管理员修改件数运费模板明细
     * @param pieceFreightBo
     * @param userId
     * @param userName
     * @return
     */
    public ReturnObject modifyPieceFreight(PieceFreightBo pieceFreightBo,Long userId,String userName)
    {
        PieceFreightPo pieceFreightPo=(PieceFreightPo) Common.cloneVo(pieceFreightBo,PieceFreightPo.class);
        Common.setPoModifiedFields(pieceFreightPo, userId, userName);
        try {
            PieceFreightPo rp=pieceFreightPoMapper.selectByPrimaryKey(pieceFreightBo.getId());
            if (rp == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            pieceFreightPoMapper.updateByPrimaryKeySelective(pieceFreightPo);
            return new ReturnObject();

        } catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
