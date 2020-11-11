package sensecloud.web.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sensecloud.web.bean.AbRole;

/**
 * @author zhangqiang
 * @since 2020/11/5 20:17
 */
@FeignClient(name = "supersetRemoteAuthService", url = "10.10.41.59:8082/v1/hadoop/")
public interface SupersetRemoteAuthService {

    @PostMapping("createRole")
    void createRole(@RequestBody AbRole role);

    @PostMapping("deleteRole")
    void deleteRole(@RequestBody AbRole role);

    @PostMapping("bindRoleToUser")
    void bindRoleToUser(@RequestParam("rolename") String rolename,
                        @RequestParam("username") String username);

    @PostMapping("unbindRoleToUser")
    void unbindRoleToUser(@RequestParam("rolename") String rolename,
                          @RequestParam("username") String username);

}
