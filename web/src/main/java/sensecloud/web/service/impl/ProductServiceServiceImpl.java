package sensecloud.web.service.impl;

import sensecloud.web.entity.ProductService;
import sensecloud.web.mapper.ProductServiceMapper;
import sensecloud.web.service.IProductServiceService;
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
public class ProductServiceServiceImpl extends ServiceImpl<ProductServiceMapper, ProductService> implements IProductServiceService {

}
