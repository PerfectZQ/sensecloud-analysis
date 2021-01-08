package sensecloud.web.bean.superset;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dashboards")
@ApiModel(value = "Dashboards对象", description = "")
public class DashboardsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dashboardId;
    private Integer userId;
    private String username;
    private LocalDateTime createdOn;
    private LocalDateTime changedOn;
    private String dashboardTitle;
    private Integer createdByFk;
    private String createdByUsername;
    private Integer changedByFk;
    private String changedByUsername;
    private String description;
    private String slug;
    private Boolean published;

}
