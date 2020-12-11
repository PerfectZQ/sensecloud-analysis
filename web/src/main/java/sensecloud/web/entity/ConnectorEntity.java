package sensecloud.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import sensecloud.web.bean.ConnectorBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @TableName(value = "connector", autoResultMap = true)
public class ConnectorEntity extends ConnectorBean {

}
