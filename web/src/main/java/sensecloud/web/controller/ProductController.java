package sensecloud.web.controller;

import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import sensecloud.web.bean.common.PageResult;
import sensecloud.web.entity.Product;
import sensecloud.web.entity.ProductService;
import sensecloud.web.service.impl.ProductServiceImpl;
import sensecloud.web.service.impl.ProductServiceServiceImpl;

import java.util.List;


/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductServiceServiceImpl productServiceService;

    @ApiOperation(value = "根据产品线名称获取服务ID")
    @GetMapping("getProductIdByName")
    public Integer getProductIdByName(@RequestParam String productName) {
        return productService.getOne(new QueryWrapper<>(new Product().setProductName(productName))).getId();
    }

    @ApiOperation(value = "分页获取产品线列表")
    @GetMapping("listProducts")
    public PageResult listProducts(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false, defaultValue = "1") int pageId,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Product query = new Product()
                .setOwner(owner)
                .setProductName(serviceName)
                .setStatus(status);
        QueryWrapper<Product> wrapper = new QueryWrapper<>(query);
        Page<Product> page = new Page<>(pageId, pageSize);
        IPage<Product> iPage = productService.page(page, wrapper);
        long totalPages = iPage.getPages();
        List<Product> metaEntities = iPage.getRecords();
        return PageResult.builder()
                .currentPageElems(metaEntities)
                .currentPageId(pageId)
                .totalPages(totalPages)
                .totalElems(iPage.getTotal())
                .build();
    }

    @ApiOperation(value = "添加或修改产品线，修改必须带着ID")
    @PostMapping("saveOrUpdateProduct")
    public void saveOrUpdateProduct(@RequestBody Product product) {
        productService.saveOrUpdate(product);
    }

    @ApiOperation(value = "删除 Product")
    @PostMapping("deleteProduct")
    public void deleteProduct(@RequestBody Product product) {
        productService.remove(new QueryWrapper<>(product));
    }

    @ApiOperation(value = "向产品线添加服务")
    @PostMapping("addServiceToProduct")
    public void addServiceToProduct(@RequestBody ProductService productService) {
        QueryWrapper<ProductService> queryWrapper = new QueryWrapper<>(productService);
        if (productServiceService.count(queryWrapper) == 0) {
            productServiceService.save(productService);
        }
    }

    @ApiOperation(value = "查询服务列表")
    @PostMapping("listServices")
    public List<ProductService> listServices(@RequestBody ProductService productService) {
        QueryWrapper<ProductService> queryWrapper = new QueryWrapper<>(productService);
        return productServiceService.list(queryWrapper);
    }

    @ApiOperation(value = "删除服务列表")
    @PostMapping("deleteService")
    public void deleteService(@RequestBody ProductService productService) {
        QueryWrapper<ProductService> queryWrapper = new QueryWrapper<>(productService);
        productServiceService.remove(queryWrapper);
    }

}
