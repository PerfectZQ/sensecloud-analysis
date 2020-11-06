package sensecloud.web.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sensecloud.web.entity.RoleComponentVO;
import sensecloud.web.entity.WebComponentRoleMappingVO;
import sensecloud.web.service.impl.RoleServiceImpl;
import sensecloud.web.service.impl.WebComponentRoleMappingServiceImpl;
import sensecloud.web.service.remote.AirflowRemoteAuthService;
import sensecloud.web.service.remote.ClickHouseRemoteAuthService;
import sensecloud.web.service.remote.SupersetRemoteAuthService;

import java.util.List;

/**
 * @author zhangqiang
 * @since 2020/11/5 16:17
 */
@RestController
@RequestMapping("/role_bind")
@Slf4j
public class RoleBindController {

    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private WebComponentRoleMappingServiceImpl webComponentRoleMappingService;

    @Autowired
    private AirflowRemoteAuthService airflowRemoteService;
    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;
    @Autowired
    private ClickHouseRemoteAuthService clickHouseRemoteAuthService;

    /**
     * 为用户绑定角色
     *
     * @param userId
     * @param roleId
     * @param groupId
     */
    @PostMapping("/user_role_bind")
    public void userRoleBind(@RequestParam Integer userId,
                             @RequestParam Integer roleId,
                             @RequestParam Integer groupId) {

    }

    @PostMapping("/initRoleAuth")
    public void initRoleAuth(@RequestParam String username,
                             @RequestParam Integer roleId) {
        RoleComponentVO roleComponentVO = roleService.getRoleComponentVO(roleId);
        String webRoleName = roleComponentVO.getRoleName();
        log.info("initRoleAuth: " + webRoleName);
        if (roleService.isWebRole(roleComponentVO)) {
            List<WebComponentRoleMappingVO> webComponentRoleMappingVOList = webComponentRoleMappingService
                    .getBaseMapper().getWebComponentRoleMappingVOByWebRoleId(roleId);
            webComponentRoleMappingVOList.forEach(webComponentRoleMappingVO -> {
                String componentName = webComponentRoleMappingVO.getComponentRoleComponentName();
                String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
                // Todo: 根据 UserID 获取 UserGroup Name
                componentRoleName = "Group".equals(componentRoleName) ? "" : componentRoleName;
                switch (componentName) {
                    case "airflow":
                        airflowRemoteService.bindRoleToUser(componentName, username);
                        break;
                    case "superset":
                        supersetRemoteAuthService.bindRoleToUser(componentName, username);
                        break;
                    case "clickhouse":
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的组件: " + componentName);
                }
            });
        } else {
            throw new IllegalArgumentException("目前只支持 WebRole 的初始化操作");
        }
    }

}
