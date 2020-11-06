package sensecloud.web.service;

import sensecloud.web.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import sensecloud.web.entity.RoleComponentVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
public interface IRoleService extends IService<Role> {

    RoleComponentVO getRoleComponentVO(Integer roleId);

    /**
     * 查看当前角色是否是 Web 角色
     *
     * @param roleComponentVO
     * @return
     */
    Boolean isWebRole(RoleComponentVO roleComponentVO);
}
