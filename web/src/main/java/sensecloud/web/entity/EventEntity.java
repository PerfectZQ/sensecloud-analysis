package sensecloud.web.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import sensecloud.web.bean.EventBean;

import java.time.LocalDateTime;

@Data @TableName(value = "event", autoResultMap = true)
public class EventEntity extends EventBean {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(typeHandler = FastjsonTypeHandler.class, exist = false)
    private FlowEntity flow;

    private Long generatorId;

    private String createBy;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;

}
