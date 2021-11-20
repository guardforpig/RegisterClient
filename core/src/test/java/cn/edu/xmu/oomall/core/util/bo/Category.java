package cn.edu.xmu.oomall.core.util.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.vo.CategoryRetVo;
import cn.edu.xmu.oomall.shop.model.po.CategoryPo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品分类Bo
 * pid为0表示一级类，大于0表示二级类，为-1表示单独类
 * @author Zhiliang Li 22920192204235
 * @date 2021/11/12
 */
@Data
@NoArgsConstructor
public class Category implements VoObject, Serializable {
    private Long id;
    private String name;
    private Integer commissionRatio;
    private Long pid;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private Long modifierId;
    private LocalDateTime gmtModified;
    private String modifierName;

    public Category(CategoryPo po){
        this.id = po.getId();
        this.pid = po.getPid();
        this.name = po.getName();
        this.commissionRatio=po.getCommissionRatio();
        this.creatorId=po.getCreatedBy();
        this.creatorName=po.getCreateName();
        this.gmtCreate=po.getGmtCreate();
        this.modifierId=po.getModifiedBy();
        this.gmtModified=po.getGmtModified();
        this.modifierName=po.getModiName();
    }
    @Override
    public Object createVo() {
        return new CategoryRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new CategoryRetVo(this);
    }

    public CategoryPo createCategoryPo() {
        CategoryPo po = new CategoryPo();
        po.setId(this.id);
        po.setName(this.name);
        po.setPid(this.pid);
        po.setModiName(this.modifierName);
        po.setModifiedBy(this.modifierId);
        po.setGmtModified(this.gmtModified);
        po.setCommissionRatio(this.commissionRatio);
        po.setGmtCreate(this.gmtCreate);
        po.setCreateName(this.creatorName);
        po.setCreatedBy(this.creatorId);
        return po;
    }
}

