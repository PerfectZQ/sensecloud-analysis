package sensecloud.web.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zhangqiang
 * @since 2020/11/5 17:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WebComponentRoleMappingVO", description = "")

public class WebComponentRoleMappingVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射关系ID，主键")
    private Integer id;

    @ApiModelProperty(value = "Web系统角色ID")
    private Integer webRoleId;

    @ApiModelProperty(value = "Web系统角色名称")
    private String webRoleName;

    private Integer webRoleComponentId;

    private String webRoleComponentName;

    @ApiModelProperty(value = "其他系统角色ID")
    private Integer componentRoleId;

    @ApiModelProperty(value = "其他系统角色名称")
    private String componentRoleName;

    private Integer componentRoleComponentId;

    private String componentRoleComponentName;

    @ApiModelProperty(value = "是否启用该映射关系，默认1，启用")
    private Boolean enabled;

}
