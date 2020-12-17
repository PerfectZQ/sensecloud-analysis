package sensecloud.web.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangqiang
 * @since 2020/12/10 18:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAuthorityVO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("UserRoleProductId")
    private Integer id;
    private Integer userId;
    private String username;
    private Integer roleId;
    private String roleName;
    private Integer productId;
    private String productName;

}
