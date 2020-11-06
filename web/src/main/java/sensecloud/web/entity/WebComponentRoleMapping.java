package sensecloud.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("web_component_role_mapping")
@ApiModel(value="WebComponentRoleMapping对象", description="")
public class WebComponentRoleMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射关系ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "其他系统角色ID")
    private Integer componentRoleId;

    @ApiModelProperty(value = "Web系统角色ID")
    private Integer webRoleId;

    @ApiModelProperty(value = "是否启用该映射关系，默认1，启用")
    private Boolean enabled;


}
