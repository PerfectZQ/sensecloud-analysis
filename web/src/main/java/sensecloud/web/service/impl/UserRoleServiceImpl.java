package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import sensecloud.web.entity.UserProduct;
import sensecloud.web.entity.UserRole;
import sensecloud.web.mapper.UserRoleMapper;
import sensecloud.web.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-10
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    public UserRole createUserRoleIfNotExist(UserRole userRole) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>(userRole);
        if (count(queryWrapper) == 0) {
            save(userRole);
        }
        return getOne(queryWrapper);
    }
}
