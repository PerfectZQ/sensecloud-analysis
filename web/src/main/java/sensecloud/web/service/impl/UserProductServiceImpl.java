package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import sensecloud.web.entity.Role;
import sensecloud.web.entity.UserProduct;
import sensecloud.web.mapper.UserProductMapper;
import sensecloud.web.service.IUserProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-10
 */
@Service
public class UserProductServiceImpl extends ServiceImpl<UserProductMapper, UserProduct> implements IUserProductService {

    public UserProduct createUserProductIfNotExist(UserProduct userProduct) {
        QueryWrapper<UserProduct> queryWrapper = new QueryWrapper<>(userProduct);
        if (count(queryWrapper) == 0) {
            save(userProduct);
        }
        return getOne(queryWrapper);
    }

}
