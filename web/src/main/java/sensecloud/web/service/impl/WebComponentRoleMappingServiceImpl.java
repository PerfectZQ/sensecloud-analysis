package sensecloud.web.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import sensecloud.web.bean.clickhouse.RequestBoundUser;
import sensecloud.web.entity.WebComponentRoleMapping;
import sensecloud.web.entity.WebComponentRoleMappingVO;
import sensecloud.web.mapper.WebComponentRoleMappingMapper;
import sensecloud.web.service.IWebComponentRoleMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import sensecloud.web.service.remote.AirflowRemoteAuthService;
import sensecloud.web.service.remote.ClickHouseRemoteAuthService;
import sensecloud.web.service.remote.SupersetRemoteAuthService;

import static sensecloud.web.constant.CommonConstant.*;
import static sensecloud.web.constant.CommonConstant.CLICK_HOUSE_COMPONENT_NAME;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZhangQiang
 * @since 2020-11-05
 */
@Slf4j
@Service
public class WebComponentRoleMappingServiceImpl extends ServiceImpl<WebComponentRoleMappingMapper, WebComponentRoleMapping> implements IWebComponentRoleMappingService {

    @Autowired
    private AirflowRemoteAuthService airflowRemoteAuthService;
    @Autowired
    private SupersetRemoteAuthService supersetRemoteAuthService;
    @Autowired
    private ClickHouseRemoteAuthService clickHouseRemoteAuthService;

    /**
     * 按照 Web 与 Component 的映射执行角色绑定操作
     *
     * @param username
     * @param productName
     * @param webComponentRoleMappingVO
     */
    public void enableWebComponentRoleMapping(String username,
                                              String productName,
                                              WebComponentRoleMappingVO webComponentRoleMappingVO) {

        String componentName = webComponentRoleMappingVO.getComponentRoleComponentName().toLowerCase();
        String webRoleName = webComponentRoleMappingVO.getWebRoleName();
        String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
        componentRoleName = AIRFLOW_SUPERSET_GROUP_ROLE_NAME.equals(componentRoleName) ? productName : componentRoleName;
        switch (componentName) {
            case AIRFLOW_COMPONENT_NAME:
                if (!AIRFLOW_COMPONENT_GITLAB.equalsIgnoreCase(componentRoleName)) {
                    log.info("====> Init " + webRoleName + " " + username + " of " + productName + " on Airflow\n" +
                            "====> assign role " + componentRoleName + " to " + username);
                    airflowRemoteAuthService.bindRoleToUser(componentRoleName, username);
                }
                break;
            case SUPERSET_COMPONENT_NAME:
                log.info("====> Init " + webRoleName + " " + username + " of " + productName + " on Superset\n" +
                        "====> assign role " + componentRoleName + " to " + username);
                supersetRemoteAuthService.bindRoleToUser(componentRoleName, username);
                break;
            case CLICK_HOUSE_COMPONENT_NAME:
                log.info("====> Init " + webRoleName + " " + username + " of " + productName + " on ClickHouse\n" +
                        "====> assign role " + componentRoleName + " to " + username);
                RequestBoundUser requestBoundUser = new RequestBoundUser()
                        .setUserName(username)
                        .setRoleName(componentRoleName)
                        .setProductLine(productName);
                clickHouseRemoteAuthService.boundUserRoleToGroup(requestBoundUser);
                break;
            default:
                throw new IllegalArgumentException("UnSupport Component: " + componentName);
        }
    }

    /**
     * 按照 Web 与 Component 的映射执行角色解绑操作
     *
     * @param username
     * @param productName
     * @param webComponentRoleMappingVO
     */
    public void disableWebComponentRoleMapping(String username,
                                               String productName,
                                               WebComponentRoleMappingVO webComponentRoleMappingVO) {

        String componentName = webComponentRoleMappingVO.getComponentRoleComponentName().toLowerCase();
        String componentRoleName = webComponentRoleMappingVO.getComponentRoleName();
        String webRoleName = webComponentRoleMappingVO.getWebRoleName();
        componentRoleName = AIRFLOW_SUPERSET_GROUP_ROLE_NAME.equals(componentRoleName) ? productName : componentRoleName;
        switch (componentName) {
            case AIRFLOW_COMPONENT_NAME:
                if (!AIRFLOW_COMPONENT_GITLAB.equalsIgnoreCase(componentRoleName)) {
                    airflowRemoteAuthService.unbindRoleToUser(componentRoleName, username);
                    log.info("====> Disable " + webRoleName + " " + username + "of" + productName + "on Airflow\n" +
                            "====> delete role " + componentRoleName + " from " + username);
                }
                break;
            case SUPERSET_COMPONENT_NAME:
                supersetRemoteAuthService.unbindRoleToUser(componentRoleName, username);
                log.info("====> Disable " + webRoleName + " " + username + "of" + productName + "on Superset\n" +
                        "====> delete role " + componentRoleName + " from " + username);
                break;
            case CLICK_HOUSE_COMPONENT_NAME:
                RequestBoundUser requestBoundUser = new RequestBoundUser()
                        .setUserName(username)
                        .setProductLine(productName)
                        .setRoleName(componentRoleName);
                log.info("====> Disable " + webRoleName + " " + username + "of" + productName + "on ClickHouse\n" +
                        "====> delete role " + componentRoleName + " from " + username);
                clickHouseRemoteAuthService.unboundUserRoleFromGroup(requestBoundUser);
                break;
            default:
                throw new IllegalArgumentException("UnSupport Component: " + componentName);
        }
    }

}
