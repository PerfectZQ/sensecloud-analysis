package sensecloud.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/20 15:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "InitGroup", description = "初始化产品线")
public class InitProduct {
    @ApiModelProperty("产品线管理员用户名")
    private String username;
    @ApiModelProperty("产品线服务名称")
    private String productName;
    @ApiModelProperty("产品线对应Gitlab库地址")
    private String repository;
    @ApiModelProperty("产品线对应Gitlab库分支")
    private String branch;
}
