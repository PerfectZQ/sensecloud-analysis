package sensecloud.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangqiang
 * @since 2020/11/20 15:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "InitGroup", description = "初始化产品线")
public class InitProduct {

    @NotBlank
    @ApiModelProperty("产品线管理员的用户名")
    private String username;
    @NotBlank
    @ApiModelProperty("产品线服务名称")
    private String productName;
    @ApiModelProperty("产品线对应 Gitlab 库地址")
    private String repository;
    @ApiModelProperty("产品线对应 Gitlab 库分支")
    private String branch;

    /**
     * 判断是否包含合法的 Git 信息
     *
     * @return
     */
    public Boolean isGitEmpty() {
        return StringUtils.isEmpty(repository) || StringUtils.isEmpty(branch);
    }
}
