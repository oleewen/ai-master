package com.ai.master.appeal.domain.entity;

import com.ai.master.appeal.domain.enums.AppealerType;
import com.ai.master.appeal.domain.valueobject.AppealerId;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 申诉方实体
 * 发起申诉的主体，可为网点、中心等
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Appealer extends BaseEntity {
    
    /** 申诉方ID */
    private AppealerId id;
    
    /** 申诉方编码 */
    private String code;
    
    /** 申诉方类型 */
    private AppealerType type;
    
    
    /**
     * 创建申诉方
     */
    public static Appealer create(String code, AppealerType type) {
        Appealer appealer = new Appealer();
        appealer.setId(AppealerId.generate());
        appealer.setCode(code);
        appealer.setType(type);
        return appealer;
    }
}