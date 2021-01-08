package sensecloud.web.bean.airflow;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-10-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GitlabRepoVO 对象", description = "")
public class GitlabRepoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "gitlab库地址")
    private String repository;

    @ApiModelProperty(value = "gitlab分支名称")
    private String branch;

    @ApiModelProperty(value = "所属role_id")
    private Integer roleId;

    @ApiModelProperty(value = "所属role_name")
    private String roleName;

    public GitlabRepo getGitlabRepo() {
        return new GitlabRepo()
                .setRoleId(roleId)
                .setRepository(repository)
                .setBranch(branch)
                .setId(id);
    }

}
