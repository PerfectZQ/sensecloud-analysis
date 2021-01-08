package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sensecloud.web.bean.AbRole;
import sensecloud.web.bean.common.PageResult;
import sensecloud.web.bean.superset.DashboardsVO;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "supersetRemoteAuthService", url = "dlink-superset-auth:8088/")
public interface SupersetRemoteAuthService {

    @PostMapping("/supersetAuth/createRole")
    void createRole(@RequestBody AbRole role);

    @PostMapping("/supersetAuth/deleteRole")
    void deleteRole(@RequestBody AbRole role);

    @PostMapping("/supersetAuth/bindRoleToUser")
    void bindRoleToUser(@RequestParam("rolename") String rolename,
                        @RequestParam("username") String username);

    @PostMapping("/supersetAuth/unbindRoleToUser")
    void unbindRoleToUser(@RequestParam("rolename") String rolename,
                          @RequestParam("username") String username);

    @PostMapping("/supersetAuth/initGroupRole")
    void initGroupRole(@RequestBody AbRole role);

    @PostMapping("/superset/dashboard/listDashboardsVO/{pageId}/{pageSize}")
    PageResult listDashboardsVO(@PathVariable Integer pageId,
                                @PathVariable Integer pageSize,
                                @RequestBody DashboardsVO dashboardsVO);

}
