package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sensecloud.web.bean.AbRole;
import sensecloud.web.bean.airflow.AirflowInitGroup;
import sensecloud.web.config.feign.OpenFeignConfig;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "airflowRemoteAuthService", url = "dlink-airflow-auth:8088/")
// @FeignClient(name = "airflowRemoteAuthService", url = "dlink-airflow-auth:8088/", configuration = OpenFeignConfig.class)
// @FeignClient(name = "airflowRemoteAuthService", url = "10.53.7.100:53395/", configuration = OpenFeignConfig.class)
public interface AirflowRemoteAuthService {

    @PostMapping("/airflowAuth/createRole")
    void createRole(@RequestBody AbRole role);

    @PostMapping("/airflowAuth/deleteRole")
    void deleteRole(@RequestBody AbRole role);

    @PostMapping("/airflowAuth/bindRoleToUser")
    void bindRoleToUser(@RequestParam("rolename") String rolename,
                        @RequestParam("username") String username);

    @PostMapping("/airflowAuth/unbindRoleToUser")
    void unbindRoleToUser(@RequestParam("rolename") String rolename,
                          @RequestParam("username") String username);

    /**
     * 初始化 Airflow Group Role
     *
     * @param initGroup
     */
    @PostMapping("/airflowAuth/initGroupRole")
    void initGroupRole(@RequestBody AirflowInitGroup initGroup);

}
