package sensecloud.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import sensecloud.web.entity.Role;
import sensecloud.web.entity.RoleComponentVO;
import sensecloud.web.mapper.RoleMapper;
import sensecloud.web.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
     * 根据 roleId
     * @param roleId
     * @return
     */
    public RoleComponentVO getRoleComponentVO(Integer roleId) {
        return this.getBaseMapper().getRoleComponentVO(roleId);
    }

    /**
     * 查看当前角色是否是 Web 角色
     *
     * @param roleComponentVO
     * @return
     */
    public Boolean isWebRole(RoleComponentVO roleComponentVO) {
        return "web".equals(roleComponentVO.getComponentName());
    }

}
