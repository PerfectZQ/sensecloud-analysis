package sensecloud.web.service.impl;

import sensecloud.web.entity.Role;
import sensecloud.web.entity.RoleComponentVO;
import sensecloud.web.mapper.RoleMapper;
import sensecloud.web.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static sensecloud.web.constant.CommonConstant.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    /**
     * 获取 ProductAdmin RoleComponentVO
     *
     * @return
     */
    public RoleComponentVO getProductManager() {
        return this.getBaseMapper().getRoleComponentVOByName(SENSE_ANALYSIS_PRODUCT_MANAGER_NAME, SENSE_ANALYSIS_WEB_COMPONENT_NAME);
    }

    /**
     * 获取 PlatformAdmin RoleComponentVO
     *
     * @return
     */
    public RoleComponentVO getAdmin() {
        return this.getBaseMapper().getRoleComponentVOByName(SENSE_ANALYSIS_ADMIN_NAME, SENSE_ANALYSIS_WEB_COMPONENT_NAME);
    }

    /**
     * 根据 roleId
     *
     * @param roleId
     * @return
     */
    public RoleComponentVO getRoleComponentVO(Integer roleId) {
        return this.getBaseMapper().getRoleComponentVOById(roleId);
    }

    /**
     * @param roleName
     * @param componentName
     * @return
     */
    public RoleComponentVO getRoleComponentVO(String roleName, String componentName) {
        return this.getBaseMapper().getRoleComponentVOByName(roleName, componentName);
    }

    /**
     * 获取 sensecloud-analysis RoleComponentVO
     *
     * @param roleName
     * @return
     */
    public RoleComponentVO getSenseAnalysisRoleComponentVO(String roleName) {
        return this.getBaseMapper().getRoleComponentVOByName(roleName, SENSE_ANALYSIS_WEB_COMPONENT_NAME);
    }

    /**
     * 查看当前角色是否是 Web 角色
     *
     * @param roleName
     * @return
     */
    public Boolean isWebRole(String roleName) {
        RoleComponentVO roleComponentVO = this.getBaseMapper().getRoleComponentVOByRoleName(roleName);
        return SENSE_ANALYSIS_WEB_COMPONENT_NAME.equalsIgnoreCase(roleComponentVO.getComponentName());
    }

}
