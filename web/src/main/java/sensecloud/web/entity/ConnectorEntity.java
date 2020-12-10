package sensecloud.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import sensecloud.web.bean.ConnectorBean;

@Data @TableName(value = "connector", autoResultMap = true)
public class ConnectorEntity extends ConnectorBean {


}
