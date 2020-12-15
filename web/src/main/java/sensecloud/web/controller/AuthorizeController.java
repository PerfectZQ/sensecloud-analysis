package sensecloud.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import sensecloud.web.bean.AbRole;
import sensecloud.web.bean.InitProduct;
import sensecloud.web.bean.airflow.AirflowInitGroup;
import sensecloud.web.bean.airflow.GitlabRepo;
import sensecloud.web.bean.clickhouse.RequestProduct;
import sensecloud.web.entity.*;
import sensecloud.web.service.impl.*;
import sensecloud.web.service.remote.AirflowRemoteAuthService;
import sensecloud.web.service.remote.ClickHouseRemoteAuthService;
import sensecloud.web.service.remote.SupersetRemoteAuthService;

import java.util.List;

/**
 * Spring Security 提供了 Spring EL 表达式，允许我们在定义 URL 路径访问(@RequestMapping)的方法上面添加注解，来控制访问权限。
 * Spring Security可用表达式对象的基类是 {@link SecurityExpressionRoot}
 *
 * @author zhangqiang
 * @see <a href="https://my.oschina.net/zimug/blog/3132076">使用 Spring EL 控制系统功能访问权限</a>
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/authorize")
@Slf4j
public class AuthorizeController {

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private WebComponentRoleMappingServiceImpl webComponentRoleMappingService;
    @Autowired
    private UserRoleServiceImpl userRoleService;
    @Autowired
    private UserProductServiceImpl userProductService;

    @Autowired
    private AirflowRemoteAuthService airflowRemoteAuthService;
    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;
    @Autowired
    private ClickHouseRemoteAuthService clickHouseRemoteAuthService;

    /**
     * 初始化组，并设置当前用户为ProductAdmin
     *
     * @param initProduct
     */
    @ApiOperation(value = "初始化产品线信息")
    @PostMapping("initProductPermissions")
    @PreAuthorize("hasRole('PlatformAdmin')")
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
    public void initProductPermissions(@RequestBody InitProduct initProduct) {

        String productName = initProduct.getProductName();
        String username = initProduct.getUsername();

        log.info("====> Init User: " + username + "...");
        User manager = userService.createUserIfNotExist(new User().setName(username));

        log.info("====> Init Product Service: " + productName + "...");
        Product product = new Product()
                .setProductName(productName)
                .setOwner(username);
        product = productService.createProductIfNotExist(product);

        log.info("====> Init User Product Relation, bind " + username + " to " + productName + "...");
        UserProduct userProduct = new UserProduct()
                .setUserId(manager.getId())
                .setProductId(product.getId());
        userProductService.createUserProductIfNotExist(userProduct);

        AbRole abRole = new AbRole().setName(productName);
        log.info("====> Init Airflow of " + productName + "...");
        airflowRemoteAuthService.initGroupRole(new AirflowInitGroup()
                .setAbRole(abRole)
                .setGitlabRepo(new GitlabRepo()
                        .setRepository(initProduct.getRepository())
                        .setBranch(initProduct.getBranch())
                )
        );
        log.info("====> Init Superset of " + productName + "...");
        supersetRemoteAuthService.initGroupRole(abRole);
        log.info("====> Init ClickHouse of " + productName + "...");
        clickHouseRemoteAuthService.initProduct(new RequestProduct()
                .setUserName(username)
                .setProductLine(productName)
        );

        log.info("====> Init Product Manager " + username + " of " + productName + "on Sense Cloud Analysis\n" +
                "====> assign ProductManger to " + username + "...");
        RoleComponentVO roleComponentVO = roleService.getProductManager();
        UserRole userRole = new UserRole()
                .setUserId(manager.getId())
                .setRoleId(roleComponentVO.getRoleId());
        userRoleService.createUserRoleIfNotExist(userRole);

        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO ->
                webComponentRoleMappingService.enableWebComponentRoleMapping(
                        username, productName, webComponentRoleMappingVO)
        );

        log.info("====> Update product service status to true.");
        product.setStatus(true);
        productService.updateProductStatus(product);
    }

    /**
     * 管理员添加用户到组，并赋予角色
     *
     * @param username
     * @param rolename    sensecloud-analysis web role name, not component role name
     * @param productName
     */
    @PostMapping("bindUserRoleToProduct")
    @PreAuthorize("hasAnyRole('ProductAdmin', 'PlatformAdmin')")
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
    public void bindUserRoleToProduct(@RequestParam String username,
                                      @RequestParam String rolename,
                                      @RequestParam String productName) {

        RoleComponentVO roleComponentVO = roleService.getSenseAnalysisRoleComponentVO(rolename);
        if (roleComponentVO == null) {
            throw new IllegalArgumentException(rolename + " is not a sensecloud-analysis web role!");
        }

        Product product = new Product().setProductName(productName);
        product = productService.getOne(new QueryWrapper<>(product));
        if (product == null) {
            throw new IllegalArgumentException("Product " + productName + " is not exist! bind " + username + ", " + rolename + " failed");
        }

        log.info("====> Init User: " + username + "...");
        User user = userService.createUserIfNotExist(new User().setName(username));

        log.info("====> Init User Product Relation, bind " + username + " to " + productName + "...");
        UserProduct userProduct = new UserProduct()
                .setUserId(user.getId())
                .setProductId(product.getId());
        userProductService.createUserProductIfNotExist(userProduct);

        log.info("====> Init " + rolename + " " + username + " of " + productName + "on Sense Cloud Analysis\n" +
                "====> assign " + rolename + " to " + username + "...");
        UserRole userRole = new UserRole()
                .setUserId(user.getId())
                .setRoleId(roleComponentVO.getRoleId());
        userRoleService.createUserRoleIfNotExist(userRole);

        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO ->
                webComponentRoleMappingService.enableWebComponentRoleMapping(
                        username, productName, webComponentRoleMappingVO)
        );
    }

    /**
     * 管理员删除用户所在组的指定角色
     *
     * @param username
     * @param rolename
     * @param productName
     */
    @PostMapping("unbindUserRoleFromProduct")
    @PreAuthorize("hasAnyRole('ProductAdmin', 'PlatformAdmin')")
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = Exception.class)
    public void unbindUserRoleFromProduct(@RequestParam String username,
                                          @RequestParam String rolename,
                                          @RequestParam String productName) {

        RoleComponentVO roleComponentVO = roleService.getSenseAnalysisRoleComponentVO(rolename);
        if (roleComponentVO == null) {
            throw new IllegalArgumentException(rolename + " is not a sensecloud-analysis web role!");
        }

        Product product = new Product()
                .setProductName(productName)
                .setOwner(username);
        product = productService.getOne(new QueryWrapper<>(product));
        if (product == null) {
            throw new IllegalArgumentException("Product " + productName + " is not exist! bind " + username + ", " +
                    rolename + " to " + productName + " failed");
        }

        User user = userService.getOne(new QueryWrapper<>(new User().setName(username)));
        if (user == null) {
            throw new IllegalArgumentException("User " + username + " is not exist! bind " + username + ", " +
                    rolename + " to " + productName + " failed");
        }

        log.info("====> Delete User Product Relation, unbind " + username + " from " + productName + "...");
        UserProduct userProduct = new UserProduct()
                .setUserId(user.getId())
                .setProductId(product.getId());
        userProductService.remove(new QueryWrapper<>(userProduct));

        log.info("====> Disable " + rolename + " " + username + " of " + productName + "on Sense Cloud Analysis\n" +
                "====> Delete " + rolename + " from " + username + "...");
        UserRole userRole = new UserRole()
                .setUserId(user.getId())
                .setRoleId(roleComponentVO.getRoleId());
        userRoleService.remove(new QueryWrapper<>(userRole));

        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO ->
                webComponentRoleMappingService.disableWebComponentRoleMapping(
                        username, productName, webComponentRoleMappingVO)
        );
    }

}
