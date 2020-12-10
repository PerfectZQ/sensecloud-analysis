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
import sensecloud.web.service.impl.ProductServiceImpl;

import java.util.List;


/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @ApiOperation(value = "获取服务列表")
    @GetMapping("getProductList")
    public PageResult getProductList(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false, defaultValue = "1") int pageId,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Product query = new Product()
                .setOwner(owner)
                .setServiceName(serviceName)
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

    @ApiOperation(value = "添加或修改服务，修改必须带着ID")
    @PostMapping("saveOrUpdateProduct")
    public void saveOrUpdateProduct(@RequestBody Product product) {
        productService.saveOrUpdate(product);
    }

    @ApiOperation(value = "删除 Product")
    @PostMapping("deleteProduct")
    public void deleteProduct(@RequestBody Product product) {
        productService.remove(new QueryWrapper<>(product));
    }

}
