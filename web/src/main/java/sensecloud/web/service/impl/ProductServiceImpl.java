package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.Product;
import sensecloud.web.mapper.ProductMapper;
import sensecloud.web.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-12-08
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    public Boolean isExists(Product product) {
        return count(new QueryWrapper<>(product)) == 0;
    }

    /**
     * 初始化产品线服务
     *
     * @param product
     * @return
     */
    public Product createProductIfNotExist(Product product) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>(product);
        if (count(queryWrapper) == 0) {
            save(product);
        }
        return getOne(queryWrapper);
    }

    /**
     * 更新 ProductService 的状态
     *
     * @param product
     * @return
     */
    public Boolean updateProductStatus(Product product) {
        Product updateProductService = new Product()
                .setId(product.getId())
                .setStatus(product.getStatus());
        return updateById(updateProductService);
    }

}
