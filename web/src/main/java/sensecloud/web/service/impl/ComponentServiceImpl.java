package sensecloud.web.service.impl;

import sensecloud.web.entity.Component;
import sensecloud.web.mapper.ComponentMapper;
import sensecloud.web.service.IComponentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
@Service
public class ComponentServiceImpl extends ServiceImpl<ComponentMapper, Component> implements IComponentService {

}
