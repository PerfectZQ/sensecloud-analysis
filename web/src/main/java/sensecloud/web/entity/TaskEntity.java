package sensecloud.web.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import sensecloud.web.bean.TaskBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @TableName(value = "task", autoResultMap = true)
public class TaskEntity extends TaskBean {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> dependencyIds = new ArrayList<String>();

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject conf;

    private Long flowId;

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
