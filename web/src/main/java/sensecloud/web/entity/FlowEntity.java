package sensecloud.web.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.security.core.Transient;
import sensecloud.flow.Task;
import sensecloud.web.bean.FlowBean;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data @TableName(value = "flow", autoResultMap = true)
public class FlowEntity extends FlowBean {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private FlowCodeEntity code;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<Task> tasks = new LinkedList<>();

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
