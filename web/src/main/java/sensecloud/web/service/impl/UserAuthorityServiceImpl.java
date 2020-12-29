package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import sensecloud.web.entity.UserAuthority;
import sensecloud.web.mapper.UserAuthorityMapper;
import sensecloud.web.service.IUserAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-17
 */
@Service
public class UserAuthorityServiceImpl extends ServiceImpl<UserAuthorityMapper, UserAuthority> implements IUserAuthorityService {

    public UserAuthority createUserProductIfNotExist(UserAuthority userRoleProduct) {
        QueryWrapper<UserAuthority> queryWrapper = new QueryWrapper<>(userRoleProduct);
        synchronized (this){
            if (count(queryWrapper) == 0) {
                save(userRoleProduct);
            } else {
                throw new IllegalArgumentException("UserAuthority is exist.");
            }
        }
        return getOne(queryWrapper);
    }

}
