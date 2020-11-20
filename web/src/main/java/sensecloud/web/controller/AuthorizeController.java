package sensecloud.web.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import sensecloud.web.bean.AbRole;
import sensecloud.web.bean.InitGroup;
import sensecloud.web.bean.airflow.AirflowInitGroup;
import sensecloud.web.bean.airflow.GitlabRepo;
import sensecloud.web.bean.clickhouse.RequestBoundUser;
import sensecloud.web.bean.clickhouse.RequestProduct;
import sensecloud.web.entity.RoleComponentVO;
import sensecloud.web.entity.WebComponentRoleMappingVO;
import sensecloud.web.service.impl.RoleServiceImpl;
import sensecloud.web.service.impl.WebComponentRoleMappingServiceImpl;
import sensecloud.web.service.remote.AirflowRemoteAuthService;
import sensecloud.web.service.remote.ClickHouseRemoteAuthService;
import sensecloud.web.service.remote.SupersetRemoteAuthService;

import java.util.List;

import static sensecloud.web.constant.CommonConstant.*;

/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/authorize")
@Slf4j
public class AuthorizeController {

    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private WebComponentRoleMappingServiceImpl webComponentRoleMappingService;

    @Autowired
    private AirflowRemoteAuthService airflowRemoteAuthService;
    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;
    @Autowired
    private ClickHouseRemoteAuthService clickHouseRemoteAuthService;

    /**
     * 管理员添加用户到组，并赋予角色
     *
     * @param username
     * @param rolename  sensecloud-analysis web role name, not component role name
     * @param groupName
     */
    @PostMapping("boundUserRoleToGroup")
    public void boundUserRoleToGroup(@RequestParam String username,
                                     @RequestParam String rolename,
                                     @RequestParam String groupName) {
        RoleComponentVO roleComponentVO = roleService.getSenseAnalysisRoleComponentVO(rolename);
        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO -> {
            String componentName = webComponentRoleMappingVO.getComponentRoleComponentName().toLowerCase();
            String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
            componentRoleName = AIRFLOW_SUPERSET_GROUP_ROLE_NAME.equals(componentRoleName) ? groupName : componentRoleName;
            switch (componentName) {
                case AIRFLOW_COMPONENT_NAME:
                    airflowRemoteAuthService.bindRoleToUser(componentRoleName, username);
                    break;
                case SUPERSET_COMPONENT_NAME:
                    supersetRemoteAuthService.bindRoleToUser(componentRoleName, username);
                    break;
                case CLICK_HOUSE_COMPONENT_NAME:
                    RequestBoundUser requestBoundUser = new RequestBoundUser()
                            .setUserName(username)
                            .setProductLine(groupName)
                            .setRoleName(rolename);
                    clickHouseRemoteAuthService.boundUserRoleToGroup(requestBoundUser);
                    break;
                default:
                    throw new IllegalArgumentException("UnSupport Component: " + componentName);
            }
        });
    }

    /**
     * 管理员删除用户所在组的指定角色
     *
     * @param username
     * @param rolename
     * @param groupName
     */
    @PostMapping("unboundUserRoleFromGroup")
    public void unboundUserRoleFromGroup(@RequestParam String username,
                                         @RequestParam String rolename,
                                         @RequestParam String groupName) {
        RoleComponentVO roleComponentVO = roleService.getSenseAnalysisRoleComponentVO(rolename);
        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO -> {
            String componentName = webComponentRoleMappingVO.getComponentRoleComponentName().toLowerCase();
            String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
            componentRoleName = AIRFLOW_SUPERSET_GROUP_ROLE_NAME.equals(componentRoleName) ? groupName : componentRoleName;
            switch (componentName) {
                case AIRFLOW_COMPONENT_NAME:
                    airflowRemoteAuthService.unbindRoleToUser(componentRoleName, username);
                    break;
                case SUPERSET_COMPONENT_NAME:
                    supersetRemoteAuthService.unbindRoleToUser(componentRoleName, username);
                    break;
                case CLICK_HOUSE_COMPONENT_NAME:
                    RequestBoundUser requestBoundUser = new RequestBoundUser()
                            .setUserName(username)
                            .setProductLine(groupName)
                            .setRoleName(rolename);
                    clickHouseRemoteAuthService.unboundUserRoleFromGroup(requestBoundUser);
                    break;
                default:
                    throw new IllegalArgumentException("UnSupport Component: " + componentName);
            }
        });
    }

    /**
     * 初始化组，并设置当前用户为产品线管理员
     *
     * @param initGroup
     */
    @PostMapping("initGroup")
    public void initGroup(@RequestBody InitGroup initGroup) {
        String groupName = initGroup.getGroupName();
        String username = initGroup.getUsername();
        RoleComponentVO roleComponentVO = roleService.getProductManager();
        AbRole abRole = new AbRole().setName(groupName);
        log.info("====> Init Airflow of " + groupName + "...");
        airflowRemoteAuthService.initGroupRole(new AirflowInitGroup()
                .setAbRole(abRole)
                .setGitlabRepo(new GitlabRepo()
                        .setRepository(initGroup.getRepository())
                        .setBranch(initGroup.getBranch())
                )
        );
        log.info("====> Init Superset of " + groupName + "...");
        supersetRemoteAuthService.initGroupRole(abRole);
        log.info("====> Init ClickHouse of " + groupName + "...");
        clickHouseRemoteAuthService.initProduct(new RequestProduct()
                .setUserName(username)
                .setProductLine(groupName)
        );
        log.info("====> Init Product Manager " + username + " of " + groupName + "...");
        List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleComponentVO.getRoleId());
        webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO -> {
            String componentName = webComponentRoleMappingVO.getComponentRoleComponentName().toLowerCase();
            String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
            componentRoleName = AIRFLOW_SUPERSET_GROUP_ROLE_NAME.equals(componentRoleName) ? groupName : componentRoleName;
            switch (componentName) {
                case AIRFLOW_COMPONENT_NAME:
                    log.info("====> Init Product Manager " + username + " of " + groupName + " on Airflow...");
                    airflowRemoteAuthService.bindRoleToUser(componentRoleName, username);
                    break;
                case SUPERSET_COMPONENT_NAME:
                    log.info("====> Init Product Manager " + username + " of " + groupName + " on Superset...");
                    supersetRemoteAuthService.bindRoleToUser(componentRoleName, username);
                    break;
                case CLICK_HOUSE_COMPONENT_NAME:
                    // 铁达在 InitProduct 接口已经初始化了，所以这里啥也不干
                    log.info("====> Init Product Manager " + username + " of " + groupName + " on ClickHouse...");
                    break;
                default:
                    throw new IllegalArgumentException("UnSupport Component: " + componentName);
            }
        });
    }
}
