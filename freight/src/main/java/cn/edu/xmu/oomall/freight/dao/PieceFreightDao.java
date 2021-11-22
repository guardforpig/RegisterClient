package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.mapper.PieceFreightPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.PieceFreight;
import cn.edu.xmu.oomall.freight.model.bo.WeightFreight;
import cn.edu.xmu.oomall.freight.model.po.PieceFreightPoExample;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Gao Yanfeng
 * @date 2021/11/22
 */
@Repository
public class PieceFreightDao {

    @Autowired
    private PieceFreightPoMapper pieceFreightPoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.freight.model.expiretime}")
    private long timeout;

    public ReturnObject getPieceItem(Long fid, List<Long> regionIds) {
        try {
            var regionId = regionIds.get(regionIds.size() - 1);
            var redisRet = redisUtil.get("region_" + regionId.toString());
            if (redisRet != null) {
                return new ReturnObject(redisRet);
            }
            for (int i = regionIds.size() - 1; i >= 0; --i) {
                var example = new PieceFreightPoExample();
                var criteria = example.createCriteria();
                criteria.andFreightModelIdEqualTo(fid).andRegionIdEqualTo(regionIds.get(i));
                var poList = pieceFreightPoMapper.selectByExample(example);
                if (!poList.isEmpty()) {
                    var bo = (PieceFreight) Common.cloneVo(poList.get(0), PieceFreight.class);
                    redisUtil.set("region_" + regionId.toString(), bo, timeout);
                    return new ReturnObject(bo);
                }
            }
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        // TODO 没有找到合适的错误码
        return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "根据指定地区和运费模板未找到需要的运费模板明细");
    }
}
